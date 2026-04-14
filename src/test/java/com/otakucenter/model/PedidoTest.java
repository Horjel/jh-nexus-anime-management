package com.otakucenter.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PedidoTest {

    @Test
    void deberiaSincronizarCompatibilidadLegacyDesdeDetalles() {
        Pedido pedido = new Pedido();
        Producto producto = new Producto();
        producto.setNombre("Figura Edward Elric 1/8");
        producto.setPrecio(new BigDecimal("29.99"));

        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("29.99"));
        detalle.setSubtotal(new BigDecimal("59.98"));

        pedido.addDetalle(detalle);
        pedido.sincronizarCompatibilidadLegacyDesdeDetalles();

        assertTrue(pedido.tieneDetalles());
        assertTrue(pedido.usaCompatibilidadLegacy());
        assertEquals(producto, pedido.getProductoLegacy());
        assertEquals(Integer.valueOf(2), pedido.getCantidadLegacy());
        assertEquals("Figura Edward Elric 1/8 x2", pedido.getResumenProductos());
        assertEquals(Integer.valueOf(2), pedido.getCantidadTotal());
    }

    @Test
    void deberiaLimpiarCompatibilidadLegacySiNoHayDetalles() {
        Pedido pedido = new Pedido();
        Producto producto = new Producto();
        producto.setNombre("Legacy");
        pedido.setProductoLegacy(producto);
        pedido.setCantidadLegacy(1);

        pedido.sincronizarCompatibilidadLegacyDesdeDetalles();

        assertFalse(pedido.tieneDetalles());
        assertFalse(pedido.usaCompatibilidadLegacy());
        assertNull(pedido.getProductoLegacy());
        assertNull(pedido.getCantidadLegacy());
        assertEquals("", pedido.getResumenProductos());
        assertEquals(Integer.valueOf(0), pedido.getCantidadTotal());
    }
}
