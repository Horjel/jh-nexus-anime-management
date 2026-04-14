package com.otakucenter.service.impl;

import com.otakucenter.dao.CategoriaDao;
import com.otakucenter.dao.PedidoDao;
import com.otakucenter.dao.ProductoDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ResourceNotFoundException;
import com.otakucenter.model.Categoria;
import com.otakucenter.model.PageResult;
import com.otakucenter.model.Producto;
import com.otakucenter.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private static final int PAGE_SIZE = 5;

    private final ProductoDao productoDao;
    private final CategoriaDao categoriaDao;
    private final PedidoDao pedidoDao;

    public ProductoServiceImpl(ProductoDao productoDao, CategoriaDao categoriaDao, PedidoDao pedidoDao) {
        this.productoDao = productoDao;
        this.categoriaDao = categoriaDao;
        this.pedidoDao = pedidoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProductos() {
        return productoDao.countAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoDao.findByCategoriaId(categoriaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarStockBajo(int maxStock, int limit) {
        return productoDao.findLowStock(maxStock, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Producto> buscarPorFiltros(String termino, boolean soloStockBajo, String orden, int pagina) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        int totalItems = (int) productoDao.countByFiltros(terminoNormalizado, soloStockBajo);
        int paginaNormalizada = normalizarPagina(pagina, totalItems);
        int offset = (paginaNormalizada - 1) * PAGE_SIZE;
        List<Producto> productos = productoDao.findPageByFiltros(terminoNormalizado, soloStockBajo, orden, offset, PAGE_SIZE);
        return PageResult.fromPage(productos, paginaNormalizada, PAGE_SIZE, totalItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarParaExportacion(String termino, boolean soloStockBajo, String orden) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        int totalItems = (int) productoDao.countByFiltros(terminoNormalizado, soloStockBajo);
        return productoDao.findPageByFiltros(terminoNormalizado, soloStockBajo, orden, 0, totalItems == 0 ? Integer.MAX_VALUE : totalItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + id));
    }

    @Override
    public Producto guardar(Producto producto, String usernameActor) {
        String nombreNormalizado = producto.getNombre() == null ? "" : producto.getNombre().trim();
        String descripcionNormalizada = producto.getDescripcion() == null ? "" : producto.getDescripcion().trim();
        producto.setNombre(nombreNormalizado);
        producto.setDescripcion(descripcionNormalizada);

        Optional<Producto> productoExistente = productoDao.findByNombre(nombreNormalizado);
        if (productoExistente.isPresent() && !productoExistente.get().getId().equals(producto.getId())) {
            throw new BusinessRuleException("Ya existe un producto con ese nombre");
        }

        Long categoriaId = producto.getCategoria() == null ? null : producto.getCategoria().getId();
        if (categoriaId == null) {
            throw new BusinessRuleException("Debes seleccionar una categoria");
        }

        Categoria categoria = categoriaDao.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("La categoria seleccionada no existe"));
        producto.setCategoria(categoria);
        aplicarAuditoria(producto, usernameActor);

        return productoDao.save(producto);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (pedidoDao.countByProductoId(id) > 0) {
            throw new BusinessRuleException("No puedes eliminar un producto con pedidos asociados");
        }
        productoDao.deleteById(id);
    }

    private void aplicarAuditoria(Producto producto, String usernameActor) {
        LocalDateTime ahora = LocalDateTime.now();
        String actor = usernameActor == null || usernameActor.trim().isEmpty() ? "SYSTEM" : usernameActor.trim();
        if (producto.getFechaCreacion() == null) {
            producto.setFechaCreacion(ahora);
        }
        if (producto.getCreadoPor() == null || producto.getCreadoPor().trim().isEmpty()) {
            producto.setCreadoPor(actor);
        }
        producto.setFechaActualizacion(ahora);
        producto.setActualizadoPor(actor);
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
