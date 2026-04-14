package com.otakucenter.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "pedidos")
public class Pedido {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Compatibilidad con la version inicial del pedido simple.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto productoLegacy;

    @Column(name = "cantidad")
    private Integer cantidadLegacy;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> detalles;

    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "creado_por", length = 60)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 60)
    private String actualizadoPor;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Pedido() {
        this.cliente = new Cliente();
        this.detalles = new ArrayList<PedidoDetalle>();
        this.estado = "PENDIENTE";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Producto getProductoLegacy() {
        return productoLegacy;
    }

    public void setProductoLegacy(Producto productoLegacy) {
        this.productoLegacy = productoLegacy;
    }

    public Integer getCantidadLegacy() {
        return cantidadLegacy;
    }

    public void setCantidadLegacy(Integer cantidadLegacy) {
        this.cantidadLegacy = cantidadLegacy;
    }

    public List<PedidoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PedidoDetalle> detalles) {
        this.detalles = detalles;
    }

    public void addDetalle(PedidoDetalle detalle) {
        detalle.setPedido(this);
        this.detalles.add(detalle);
    }

    public boolean tieneDetalles() {
        return detalles != null && !detalles.isEmpty();
    }

    public boolean usaCompatibilidadLegacy() {
        return productoLegacy != null && cantidadLegacy != null;
    }

    public void limpiarCompatibilidadLegacy() {
        this.productoLegacy = null;
        this.cantidadLegacy = null;
    }

    public void sincronizarCompatibilidadLegacyDesdeDetalles() {
        if (!tieneDetalles()) {
            limpiarCompatibilidadLegacy();
            return;
        }

        PedidoDetalle primerDetalle = detalles.get(0);
        this.productoLegacy = primerDetalle.getProducto();
        this.cantidadLegacy = primerDetalle.getCantidad();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }

    public void setActualizadoPor(String actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getFechaPedidoTexto() {
        if (fechaPedido == null) {
            return "";
        }
        return fechaPedido.format(FORMATTER);
    }

    public String getFechaCreacionTexto() {
        return fechaCreacion == null ? "" : fechaCreacion.format(FORMATTER);
    }

    public String getFechaActualizacionTexto() {
        return fechaActualizacion == null ? "" : fechaActualizacion.format(FORMATTER);
    }

    public String getResumenProductos() {
        if (tieneDetalles()) {
            StringBuilder resumen = new StringBuilder();
            for (int i = 0; i < detalles.size(); i++) {
                PedidoDetalle detalle = detalles.get(i);
                if (i > 0) {
                    resumen.append(" | ");
                }
                resumen.append(detalle.getProducto().getNombre())
                        .append(" x")
                        .append(detalle.getCantidad());
            }
            return resumen.toString();
        }

        if (usaCompatibilidadLegacy()) {
            return productoLegacy.getNombre() + " x" + cantidadLegacy;
        }

        return "";
    }

    public Integer getCantidadTotal() {
        if (tieneDetalles()) {
            int totalCantidad = 0;
            for (PedidoDetalle detalle : detalles) {
                totalCantidad += detalle.getCantidad();
            }
            return totalCantidad;
        }

        return cantidadLegacy == null ? 0 : cantidadLegacy;
    }
}
