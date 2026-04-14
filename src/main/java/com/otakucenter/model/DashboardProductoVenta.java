package com.otakucenter.model;

public class DashboardProductoVenta {

    private final Producto producto;
    private final int unidadesVendidas;

    public DashboardProductoVenta(Producto producto, int unidadesVendidas) {
        this.producto = producto;
        this.unidadesVendidas = unidadesVendidas;
    }

    public DashboardProductoVenta(Producto producto, Long unidadesVendidas) {
        this(producto, unidadesVendidas == null ? 0 : unidadesVendidas.intValue());
    }

    public Producto getProducto() {
        return producto;
    }

    public int getUnidadesVendidas() {
        return unidadesVendidas;
    }
}
