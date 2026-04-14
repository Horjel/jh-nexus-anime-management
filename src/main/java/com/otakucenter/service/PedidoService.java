package com.otakucenter.service;

import com.otakucenter.model.DashboardProductoVenta;
import com.otakucenter.model.PageResult;
import com.otakucenter.model.PedidoForm;
import com.otakucenter.model.Pedido;
import java.math.BigDecimal;
import java.util.List;

public interface PedidoService {

    List<Pedido> listarTodos();

    long contarPedidos();

    BigDecimal obtenerVentasTotales();

    long contarPedidosPorEstado(String estado);

    List<Pedido> listarRecientes(int limit);

    List<DashboardProductoVenta> listarTopProductosVendidos(int limit);

    PageResult<Pedido> buscarPorFiltros(String termino, String estado, String orden, int pagina);

    List<Pedido> listarParaExportacion(String termino, String estado, String orden);

    Pedido buscarPorId(Long id);

    PedidoForm crearFormulario();

    PedidoForm crearFormularioDesdePedido(Long id);

    Pedido guardar(PedidoForm pedidoForm, String usernameActor);

    void eliminarPorId(Long id);
}
