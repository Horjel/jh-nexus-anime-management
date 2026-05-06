package com.otakucenter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.otakucenter.dao.ClienteDao;
import com.otakucenter.dao.PedidoDao;
import com.otakucenter.dao.ProductoDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ResourceNotFoundException;
import com.otakucenter.model.Cliente;
import com.otakucenter.model.Pedido;
import com.otakucenter.model.PedidoDetalle;
import com.otakucenter.model.PedidoForm;
import com.otakucenter.model.PedidoLineaForm;
import com.otakucenter.model.Producto;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoDao pedidoDao;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private ProductoDao productoDao;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Test
    void deberiaGuardarPedidoValidoConUnaLineaYDescontarStock() {
        Cliente cliente = crearCliente(7L);
        Producto producto = crearProducto(3L, "Figura Edward Elric 1/8", "29.99", 5);
        PedidoForm form = crearFormulario(7L, "PENDIENTE", linea(3L, 2));

        when(clienteDao.findById(7L)).thenReturn(Optional.of(cliente));
        when(productoDao.findByIdForUpdate(3L)).thenReturn(Optional.of(producto));
        when(pedidoDao.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.guardar(form, "admin");

        assertSame(cliente, resultado.getCliente());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals(new BigDecimal("59.98"), resultado.getTotal());
        assertEquals(Integer.valueOf(2), resultado.getCantidadTotal());
        assertTrue(resultado.tieneDetalles());
        assertEquals(3, producto.getStock().intValue());

        PedidoDetalle detalle = resultado.getDetalles().get(0);
        assertSame(producto, detalle.getProducto());
        assertEquals(Integer.valueOf(2), detalle.getCantidad());
        assertEquals(new BigDecimal("29.99"), detalle.getPrecioUnitario());
        assertEquals(new BigDecimal("59.98"), detalle.getSubtotal());

        verify(productoDao).save(producto);
        verify(pedidoDao).save(resultado);
    }

    @Test
    void deberiaGuardarPedidoValidoConVariasLineasYSumarTotalCorrectamente() {
        Cliente cliente = crearCliente(1L);
        Producto edward = crearProducto(10L, "Figura Edward Elric 1/8", "29.99", 4);
        Producto poster = crearProducto(11L, "Poster de Frieren bajo la luna", "15.50", 8);
        PedidoForm form = crearFormulario(1L, "ENVIADO", linea(10L, 2), linea(11L, 1));

        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoDao.findByIdForUpdate(10L)).thenReturn(Optional.of(edward));
        when(productoDao.findByIdForUpdate(11L)).thenReturn(Optional.of(poster));
        when(pedidoDao.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.guardar(form, "delegado");

        assertEquals(2, resultado.getDetalles().size());
        assertEquals(new BigDecimal("75.48"), resultado.getTotal());
        assertEquals(Integer.valueOf(3), resultado.getCantidadTotal());
        assertEquals(2, edward.getStock().intValue());
        assertEquals(7, poster.getStock().intValue());
        verify(productoDao, times(2)).save(any(Producto.class));
    }

    @Test
    void deberiaFallarSiNoHayStockSuficiente() {
        Cliente cliente = crearCliente(1L);
        Producto producto = crearProducto(3L, "Figura Edward Elric 1/8", "29.99", 1);
        PedidoForm form = crearFormulario(1L, "PENDIENTE", linea(3L, 2));

        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoDao.findByIdForUpdate(3L)).thenReturn(Optional.of(producto));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoService.guardar(form, "admin"));

        assertEquals("No hay stock suficiente para ese pedido", ex.getMessage());
        verify(productoDao, never()).save(any(Producto.class));
        verify(pedidoDao, never()).save(any(Pedido.class));
        assertEquals(1, producto.getStock().intValue());
    }

    @Test
    void deberiaFallarSiElClienteNoExiste() {
        PedidoForm form = crearFormulario(99L, "PENDIENTE", linea(3L, 1));

        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.guardar(form, "admin"));

        verifyNoInteractions(productoDao);
        verify(pedidoDao, never()).save(any(Pedido.class));
    }

    @Test
    void deberiaFallarSiUnoDeLosProductosNoExiste() {
        Cliente cliente = crearCliente(1L);
        PedidoForm form = crearFormulario(1L, "PENDIENTE", linea(999L, 1));

        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoDao.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.guardar(form, "admin"));

        verify(productoDao, never()).save(any(Producto.class));
        verify(pedidoDao, never()).save(any(Pedido.class));
    }

    @Test
    void deberiaFallarSiNoHayLineasValidas() {
        PedidoForm form = new PedidoForm();
        form.setClienteId(1L);
        form.setEstado("PENDIENTE");
        form.setLineas(Collections.singletonList(new PedidoLineaForm()));

        Cliente cliente = crearCliente(1L);
        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoService.guardar(form, "admin"));

        assertEquals("Debes informar al menos una linea de pedido", ex.getMessage());
        verifyNoInteractions(productoDao);
        verify(pedidoDao, never()).save(any(Pedido.class));
    }

    @Test
    void deberiaEliminarPedidoYRestaurarStock() {
        Producto producto = crearProducto(4L, "Figura Alphonse Elric armor edition", "64.95", 1);
        Pedido pedido = new Pedido();
        pedido.setId(9L);

        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("64.95"));
        detalle.setSubtotal(new BigDecimal("129.90"));
        pedido.addDetalle(detalle);

        when(pedidoDao.findById(9L)).thenReturn(Optional.of(pedido));
        when(productoDao.findByIdForUpdate(4L)).thenReturn(Optional.of(producto));

        pedidoService.eliminarPorId(9L);

        assertEquals(3, producto.getStock().intValue());
        verify(productoDao).save(producto);
        verify(pedidoDao).delete(pedido);
    }

    @Test
    void deberiaEditarPedidoRestaurandoYReaplicandoStock() {
        Cliente cliente = crearCliente(1L);
        Producto productoExistente = crearProducto(4L, "Figura Alphonse Elric armor edition", "64.95", 1);
        Producto productoActualizado = crearProducto(4L, "Figura Alphonse Elric armor edition", "64.95", 3);
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(55L);
        pedidoExistente.setCliente(cliente);
        pedidoExistente.setEstado("PENDIENTE");

        PedidoDetalle detalleActual = new PedidoDetalle();
        detalleActual.setProducto(productoExistente);
        detalleActual.setCantidad(2);
        detalleActual.setPrecioUnitario(new BigDecimal("64.95"));
        detalleActual.setSubtotal(new BigDecimal("129.90"));
        pedidoExistente.addDetalle(detalleActual);

        PedidoForm form = crearFormulario(1L, "ENVIADO", linea(4L, 1));
        form.setId(55L);

        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoDao.findById(55L)).thenReturn(Optional.of(pedidoExistente));
        when(productoDao.findByIdForUpdate(4L))
                .thenReturn(Optional.of(productoExistente))
                .thenReturn(Optional.of(productoActualizado));
        when(pedidoDao.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.guardar(form, "admin");

        assertEquals("ENVIADO", resultado.getEstado());
        assertEquals(Integer.valueOf(1), resultado.getCantidadTotal());
        assertEquals(3, productoExistente.getStock().intValue());
        assertEquals(2, productoActualizado.getStock().intValue());
        verify(productoDao, times(2)).save(any(Producto.class));
    }

    private PedidoForm crearFormulario(Long clienteId, String estado, PedidoLineaForm... lineas) {
        PedidoForm form = new PedidoForm();
        form.setClienteId(clienteId);
        form.setEstado(estado);
        form.setLineas(Arrays.asList(lineas));
        return form;
    }

    private PedidoLineaForm linea(Long productoId, Integer cantidad) {
        PedidoLineaForm linea = new PedidoLineaForm();
        linea.setProductoId(productoId);
        linea.setCantidad(cantidad);
        return linea;
    }

    private Cliente crearCliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombre("Jon");
        cliente.setApellidos("Salchichon Ranbo");
        return cliente;
    }

    private Producto crearProducto(Long id, String nombre, String precio, int stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(new BigDecimal(precio));
        producto.setStock(stock);
        return producto;
    }
}
