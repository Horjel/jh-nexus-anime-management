package com.otakucenter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.otakucenter.dao.CategoriaDao;
import com.otakucenter.dao.PedidoDao;
import com.otakucenter.dao.ProductoDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ResourceNotFoundException;
import com.otakucenter.model.Categoria;
import com.otakucenter.model.Producto;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoDao productoDao;

    @Mock
    private CategoriaDao categoriaDao;

    @Mock
    private PedidoDao pedidoDao;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void deberiaGuardarProductoValidoNormalizandoTextoYAuditoria() {
        Categoria categoria = crearCategoria(2L, "Figuras de escala");
        Producto producto = crearProducto(null, "  Figura Edward Elric 1/8  ", "  Figura coleccionable  ", "29.99", 4, 2L);

        when(productoDao.findByNombre("Figura Edward Elric 1/8")).thenReturn(Optional.empty());
        when(categoriaDao.findById(2L)).thenReturn(Optional.of(categoria));
        when(productoDao.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.guardar(producto, "admin");

        assertEquals("Figura Edward Elric 1/8", resultado.getNombre());
        assertEquals("Figura coleccionable", resultado.getDescripcion());
        assertSame(categoria, resultado.getCategoria());
        assertEquals("admin", resultado.getCreadoPor());
        assertEquals("admin", resultado.getActualizadoPor());
        assertTrue(resultado.getFechaCreacion() != null);
        assertTrue(resultado.getFechaActualizacion() != null);

        verify(productoDao).save(resultado);
    }

    @Test
    void deberiaRechazarProductoDuplicadoConIdDistinto() {
        Producto existente = crearProducto(10L, "Figura Edward Elric 1/8", "existente", "29.99", 3, 2L);
        Producto nuevo = crearProducto(null, "Figura Edward Elric 1/8", "nuevo", "35.00", 1, 2L);

        when(productoDao.findByNombre("Figura Edward Elric 1/8")).thenReturn(Optional.of(existente));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.guardar(nuevo, "admin"));

        assertEquals("Ya existe un producto con ese nombre", ex.getMessage());
        verifyNoInteractions(categoriaDao);
        verify(productoDao, never()).save(any(Producto.class));
    }

    @Test
    void deberiaRechazarProductoSinCategoria() {
        Producto producto = crearProducto(null, "Poster de Frieren", "Poster premium", "15.50", 8, null);
        producto.setCategoria(null);

        when(productoDao.findByNombre("Poster de Frieren")).thenReturn(Optional.empty());

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.guardar(producto, "admin"));

        assertEquals("Debes seleccionar una categoria", ex.getMessage());
        verifyNoInteractions(categoriaDao);
        verify(productoDao, never()).save(any(Producto.class));
    }

    @Test
    void deberiaFallarSiLaCategoriaNoExiste() {
        Producto producto = crearProducto(null, "Poster de Frieren", "Poster premium", "15.50", 8, 99L);

        when(productoDao.findByNombre("Poster de Frieren")).thenReturn(Optional.empty());
        when(categoriaDao.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoService.guardar(producto, "admin"));

        assertEquals("La categoria seleccionada no existe", ex.getMessage());
        verify(productoDao, never()).save(any(Producto.class));
    }

    @Test
    void deberiaPermitirGuardarMismoNombreSiEsElMismoProducto() {
        Producto producto = crearProducto(10L, "Figura Edward Elric 1/8", "edicion revisada", "31.99", 5, 2L);
        Producto existente = crearProducto(10L, "Figura Edward Elric 1/8", "anterior", "29.99", 4, 2L);
        Categoria categoria = crearCategoria(2L, "Figuras de escala");

        when(productoDao.findByNombre("Figura Edward Elric 1/8")).thenReturn(Optional.of(existente));
        when(categoriaDao.findById(2L)).thenReturn(Optional.of(categoria));
        when(productoDao.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.guardar(producto, "delegado");

        assertSame(categoria, resultado.getCategoria());
        verify(productoDao).save(producto);
    }

    @Test
    void deberiaImpedirEliminarProductoConPedidosAsociados() {
        when(pedidoDao.countByProductoId(7L)).thenReturn(2L);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.eliminarPorId(7L));

        assertEquals("No puedes eliminar un producto con pedidos asociados", ex.getMessage());
        verify(productoDao, never()).deleteById(7L);
    }

    @Test
    void deberiaEliminarProductoSinPedidosAsociados() {
        when(pedidoDao.countByProductoId(7L)).thenReturn(0L);

        productoService.eliminarPorId(7L);

        verify(productoDao).deleteById(7L);
    }

    @Test
    void deberiaBuscarProductoPorIdODevolverNotFound() {
        Producto producto = crearProducto(4L, "Artbook heroes shonen selection", "Compendio visual", "27.80", 5, 3L);
        when(productoDao.findById(4L)).thenReturn(Optional.of(producto));
        when(productoDao.findById(999L)).thenReturn(Optional.empty());

        assertSame(producto, productoService.buscarPorId(4L));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoService.buscarPorId(999L));
        assertEquals("No existe el producto con id 999", ex.getMessage());
    }

    private Producto crearProducto(Long id, String nombre, String descripcion, String precio, int stock, Long categoriaId) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(new BigDecimal(precio));
        producto.setStock(stock);
        if (categoriaId != null) {
            Categoria categoria = new Categoria();
            categoria.setId(categoriaId);
            producto.setCategoria(categoria);
        }
        return producto;
    }

    private Categoria crearCategoria(Long id, String nombre) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        return categoria;
    }
}
