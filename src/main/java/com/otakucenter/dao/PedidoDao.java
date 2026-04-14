package com.otakucenter.dao;

import com.otakucenter.model.DashboardProductoVenta;
import com.otakucenter.model.Pedido;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PedidoDao {

    List<Pedido> findAll();

    long countAll();

    BigDecimal sumTotalVentas();

    long countByEstado(String estado);

    long countByFiltros(String termino, String estado);

    List<Pedido> findPageByFiltros(String termino, String estado, String orden, int offset, int limit);

    List<Pedido> findRecent(int limit);

    List<DashboardProductoVenta> findTopProductosVendidos(int limit);

    List<Pedido> findByFiltros(String termino, String estado);

    Optional<Pedido> findById(Long id);

    Pedido save(Pedido pedido);

    void delete(Pedido pedido);

    long countByClienteId(Long clienteId);

    long countByProductoId(Long productoId);
}
