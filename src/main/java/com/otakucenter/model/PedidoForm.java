package com.otakucenter.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PedidoForm {

    private static final int MIN_LINEAS = 3;

    private Long id;

    @NotNull(message = "Debes seleccionar un cliente")
    private Long clienteId;

    @NotBlank(message = "Debes indicar un estado")
    private String estado;

    @Valid
    private List<PedidoLineaForm> lineas;

    public PedidoForm() {
        this.lineas = new ArrayList<PedidoLineaForm>();
        this.estado = "PENDIENTE";
        asegurarLineasMinimas();
    }

    public void asegurarLineasMinimas() {
        if (lineas == null) {
            lineas = new ArrayList<PedidoLineaForm>();
        }
        while (lineas.size() < MIN_LINEAS) {
            lineas.add(new PedidoLineaForm());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<PedidoLineaForm> getLineas() {
        return lineas;
    }

    public void setLineas(List<PedidoLineaForm> lineas) {
        this.lineas = lineas;
    }

    @AssertTrue(message = "Debes informar al menos una linea valida con producto y cantidad.")
    public boolean isLineasValidas() {
        if (lineas == null || lineas.isEmpty()) {
            return false;
        }

        boolean tieneLineaCompleta = false;
        for (PedidoLineaForm linea : lineas) {
            if (linea == null) {
                continue;
            }

            boolean productoInformado = linea.getProductoId() != null;
            boolean cantidadInformada = linea.getCantidad() != null;

            if (!productoInformado && !cantidadInformada) {
                continue;
            }

            if (!productoInformado || !cantidadInformada || linea.getCantidad() < 1) {
                return false;
            }

            tieneLineaCompleta = true;
        }

        return tieneLineaCompleta;
    }
}
