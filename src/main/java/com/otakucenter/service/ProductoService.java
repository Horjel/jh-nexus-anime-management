package com.otakucenter.service;

import com.otakucenter.model.PageResult;
import com.otakucenter.model.Producto;

import java.util.List;

public interface ProductoService {

    List<Producto> listarTodos();

    long contarProductos();

    List<Producto> listarPorCategoria(Long categoriaId);

    List<Producto> listarStockBajo(int maxStock, int limit);

    PageResult<Producto> buscarPorFiltros(String termino, boolean soloStockBajo, String orden, int pagina);

    List<Producto> listarParaExportacion(String termino, boolean soloStockBajo, String orden);

    Producto buscarPorId(Long id);

    Producto guardar(Producto producto, String usernameActor);

    void eliminarPorId(Long id);
}
