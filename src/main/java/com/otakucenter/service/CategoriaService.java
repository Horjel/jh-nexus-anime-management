package com.otakucenter.service;

import com.otakucenter.model.Categoria;
import com.otakucenter.model.PageResult;

import java.util.List;

public interface CategoriaService {

    List<Categoria> listarTodas();

    long contarCategorias();

    PageResult<Categoria> buscarPorTermino(String termino, String orden, int pagina);

    List<Categoria> listarParaExportacion(String termino, String orden);

    Categoria buscarPorId(Long id);

    Categoria guardar(Categoria categoria, String usernameActor);

    void eliminarPorId(Long id);
}
