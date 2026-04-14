package com.otakucenter.controller;

import com.otakucenter.model.DashboardProductoVenta;
import com.otakucenter.model.Pedido;
import com.otakucenter.model.Producto;
import com.otakucenter.service.CategoriaService;
import com.otakucenter.service.ClienteService;
import com.otakucenter.service.PedidoService;
import com.otakucenter.service.ProductoService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final int STOCK_BAJO_MAXIMO = 5;
    private static final int MAX_PEDIDOS_RECIENTES = 5;
    private static final int MAX_TOP_PRODUCTOS = 5;

    private final CategoriaService categoriaService;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public HomeController(
            CategoriaService categoriaService,
            ProductoService productoService,
            ClienteService clienteService,
            PedidoService pedidoService
    ) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        long totalPedidos = pedidoService.contarPedidos();
        BigDecimal ventasTotales = pedidoService.obtenerVentasTotales();
        long pedidosPendientes = pedidoService.contarPedidosPorEstado("PENDIENTE");
        long pedidosEnviados = pedidoService.contarPedidosPorEstado("ENVIADO");
        long pedidosEntregados = pedidoService.contarPedidosPorEstado("ENTREGADO");
        List<Producto> productosBajoStock = productoService.listarStockBajo(STOCK_BAJO_MAXIMO, MAX_TOP_PRODUCTOS);
        List<Pedido> pedidosRecientes = pedidoService.listarRecientes(MAX_PEDIDOS_RECIENTES);
        List<DashboardProductoVenta> topProductosVendidos = pedidoService.listarTopProductosVendidos(MAX_TOP_PRODUCTOS);

        BigDecimal ticketMedio = totalPedidos == 0
                ? BigDecimal.ZERO
                : ventasTotales.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP);

        model.addAttribute("totalCategorias", categoriaService.contarCategorias());
        model.addAttribute("totalProductos", productoService.contarProductos());
        model.addAttribute("totalClientes", clienteService.contarClientes());
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("ventasTotales", ventasTotales.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("ticketMedio", ticketMedio);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("pedidosEnviados", pedidosEnviados);
        model.addAttribute("pedidosEntregados", pedidosEntregados);
        model.addAttribute("productosBajoStock", productosBajoStock);
        model.addAttribute("pedidosRecientes", pedidosRecientes);
        model.addAttribute("topProductosVendidos", topProductosVendidos);
        return "index";
    }
}
