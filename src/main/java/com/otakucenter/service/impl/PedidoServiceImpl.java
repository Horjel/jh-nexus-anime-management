package com.otakucenter.service.impl;

import com.otakucenter.dao.ClienteDao;
import com.otakucenter.dao.PedidoDao;
import com.otakucenter.dao.ProductoDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ResourceNotFoundException;
import com.otakucenter.model.Cliente;
import com.otakucenter.model.DashboardProductoVenta;
import com.otakucenter.model.PageResult;
import com.otakucenter.model.PedidoDetalle;
import com.otakucenter.model.PedidoForm;
import com.otakucenter.model.PedidoLineaForm;
import com.otakucenter.model.Pedido;
import com.otakucenter.model.Producto;
import com.otakucenter.service.PedidoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private static final List<String> ESTADOS_VALIDOS = Arrays.asList("PENDIENTE", "ENVIADO", "ENTREGADO");
    private static final int PAGE_SIZE = 5;

    private final PedidoDao pedidoDao;
    private final ClienteDao clienteDao;
    private final ProductoDao productoDao;

    public PedidoServiceImpl(PedidoDao pedidoDao, ClienteDao clienteDao, ProductoDao productoDao) {
        this.pedidoDao = pedidoDao;
        this.clienteDao = clienteDao;
        this.productoDao = productoDao;
    }

    @Override
    public List<Pedido> listarTodos() {
        return pedidoDao.findAll();
    }

    @Override
    public long contarPedidos() {
        return pedidoDao.countAll();
    }

    @Override
    public BigDecimal obtenerVentasTotales() {
        return pedidoDao.sumTotalVentas();
    }

    @Override
    public long contarPedidosPorEstado(String estado) {
        return pedidoDao.countByEstado(estado);
    }

    @Override
    public List<Pedido> listarRecientes(int limit) {
        return pedidoDao.findRecent(limit);
    }

    @Override
    public List<DashboardProductoVenta> listarTopProductosVendidos(int limit) {
        return pedidoDao.findTopProductosVendidos(limit);
    }

    @Override
    public PageResult<Pedido> buscarPorFiltros(String termino, String estado, String orden, int pagina) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        String estadoNormalizado = estado == null ? "" : estado.trim();
        int totalItems = (int) pedidoDao.countByFiltros(terminoNormalizado, estadoNormalizado);
        int paginaNormalizada = normalizarPagina(pagina, totalItems);
        int offset = (paginaNormalizada - 1) * PAGE_SIZE;
        List<Pedido> pedidos = pedidoDao.findPageByFiltros(terminoNormalizado, estadoNormalizado, orden, offset, PAGE_SIZE);
        return PageResult.fromPage(pedidos, paginaNormalizada, PAGE_SIZE, totalItems);
    }

    @Override
    public List<Pedido> listarParaExportacion(String termino, String estado, String orden) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        String estadoNormalizado = estado == null ? "" : estado.trim();
        int totalItems = (int) pedidoDao.countByFiltros(terminoNormalizado, estadoNormalizado);
        return pedidoDao.findPageByFiltros(terminoNormalizado, estadoNormalizado, orden, 0, totalItems == 0 ? Integer.MAX_VALUE : totalItems);
    }

    @Override
    public Pedido buscarPorId(Long id) {
        return pedidoDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el pedido con id " + id));
    }

    @Override
    public PedidoForm crearFormulario() {
        return new PedidoForm();
    }

    @Override
    public PedidoForm crearFormularioDesdePedido(Long id) {
        Pedido pedido = buscarPorId(id);
        PedidoForm formulario = new PedidoForm();
        formulario.setId(pedido.getId());
        formulario.setClienteId(pedido.getCliente().getId());
        formulario.setEstado(pedido.getEstado());
        formulario.setLineas(new ArrayList<PedidoLineaForm>());

        if (pedido.tieneDetalles()) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                PedidoLineaForm linea = new PedidoLineaForm();
                linea.setProductoId(detalle.getProducto().getId());
                linea.setCantidad(detalle.getCantidad());
                formulario.getLineas().add(linea);
            }
        } else if (pedido.usaCompatibilidadLegacy()) {
            PedidoLineaForm linea = new PedidoLineaForm();
            linea.setProductoId(pedido.getProductoLegacy().getId());
            linea.setCantidad(pedido.getCantidadLegacy());
            formulario.getLineas().add(linea);
        }

        formulario.asegurarLineasMinimas();
        return formulario;
    }

    @Override
    public Pedido guardar(PedidoForm pedidoForm, String usernameActor) {
        Long clienteId = pedidoForm.getClienteId();
        if (clienteId == null) {
            throw new BusinessRuleException("Debes seleccionar un cliente");
        }
        if (!ESTADOS_VALIDOS.contains(pedidoForm.getEstado())) {
            throw new BusinessRuleException("El estado seleccionado no es valido");
        }

        Cliente cliente = clienteDao.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente seleccionado no existe"));

        List<PedidoLineaForm> lineasValidas = extraerLineasValidas(pedidoForm);
        if (lineasValidas.isEmpty()) {
            throw new BusinessRuleException("Debes informar al menos una linea de pedido");
        }

        Pedido pedido = pedidoForm.getId() == null ? new Pedido() : buscarPorId(pedidoForm.getId());
        boolean nuevo = pedido.getId() == null;
        if (pedido.getId() != null) {
            restaurarStockDePedido(pedido);
            pedido.getDetalles().clear();
            pedido.limpiarCompatibilidadLegacy();
        }

        pedido.setCliente(cliente);
        pedido.setEstado(pedidoForm.getEstado());
        pedido.setFechaPedido(pedido.getFechaPedido() == null ? LocalDateTime.now() : pedido.getFechaPedido());
        pedido.setTotal(BigDecimal.ZERO);
        aplicarAuditoriaPedido(pedido, usernameActor, nuevo);

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoLineaForm lineaForm : lineasValidas) {
            Producto producto = cargarProductoParaStock(lineaForm.getProductoId());
            descontarStock(producto, lineaForm.getCantidad());

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setProducto(producto);
            detalle.setCantidad(lineaForm.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(lineaForm.getCantidad())));

            pedido.addDetalle(detalle);
            total = total.add(detalle.getSubtotal());
        }

        pedido.sincronizarCompatibilidadLegacyDesdeDetalles();
        pedido.setTotal(total);
        return pedidoDao.save(pedido);
    }

    @Override
    public void eliminarPorId(Long id) {
        Pedido pedido = buscarPorId(id);
        restaurarStockDePedido(pedido);
        pedidoDao.delete(pedido);
    }

    private List<PedidoLineaForm> extraerLineasValidas(PedidoForm pedidoForm) {
        List<PedidoLineaForm> lineasValidas = new ArrayList<PedidoLineaForm>();

        if (pedidoForm.getLineas() == null) {
            return lineasValidas;
        }

        for (PedidoLineaForm linea : pedidoForm.getLineas()) {
            Long productoId = linea.getProductoId();
            Integer cantidad = linea.getCantidad();
            boolean sinDatos = productoId == null && cantidad == null;
            if (sinDatos) {
                continue;
            }
            if (productoId == null || cantidad == null) {
                throw new BusinessRuleException("Cada linea debe tener producto y cantidad");
            }
            if (cantidad < 1) {
                throw new BusinessRuleException("La cantidad de cada linea debe ser al menos 1");
            }
            lineasValidas.add(linea);
        }

        return lineasValidas;
    }

    private void restaurarStockDePedido(Pedido pedido) {
        if (pedido.tieneDetalles()) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                restaurarStock(cargarProductoParaStock(detalle.getProducto().getId()), detalle.getCantidad());
            }
            return;
        }

        if (pedido.usaCompatibilidadLegacy()) {
            restaurarStock(cargarProductoParaStock(pedido.getProductoLegacy().getId()), pedido.getCantidadLegacy());
        }
    }

    private Producto cargarProductoParaStock(Long productoId) {
        return productoDao.findByIdForUpdate(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Uno de los productos seleccionados no existe"));
    }

    private void descontarStock(Producto producto, Integer cantidad) {
        if (producto.getStock() < cantidad) {
            throw new BusinessRuleException("No hay stock suficiente para ese pedido");
        }
        producto.setStock(producto.getStock() - cantidad);
        productoDao.save(producto);
    }

    private void restaurarStock(Producto producto, Integer cantidad) {
        producto.setStock(producto.getStock() + cantidad);
        productoDao.save(producto);
    }

    private void aplicarAuditoriaPedido(Pedido pedido, String usernameActor, boolean nuevo) {
        LocalDateTime ahora = LocalDateTime.now();
        String actor = usernameActor == null || usernameActor.trim().isEmpty() ? "SYSTEM" : usernameActor.trim();
        if (nuevo || pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(ahora);
        }
        if (nuevo || pedido.getCreadoPor() == null || pedido.getCreadoPor().trim().isEmpty()) {
            pedido.setCreadoPor(actor);
        }
        pedido.setFechaActualizacion(ahora);
        pedido.setActualizadoPor(actor);
    }

    private int normalizarPagina(int paginaSolicitada, int totalItems) {
        if (totalItems <= 0) {
            return 1;
        }
        int totalPaginas = (int) Math.ceil((double) totalItems / (double) PAGE_SIZE);
        if (paginaSolicitada < 1) {
            return 1;
        }
        return Math.min(paginaSolicitada, totalPaginas);
    }
}
