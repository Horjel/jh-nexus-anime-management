package com.otakucenter.dao;

import com.otakucenter.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaDao {

    List<Categoria> findAll();

    long countAll();

    long countByNombreContaining(String termino);

    List<Categoria> findPageByNombreContaining(String termino, String orden, int offset, int limit);

    List<Categoria> findByNombreContaining(String termino);

    Optional<Categoria> findById(Long id);

    Optional<Categoria> findByNombre(String nombre);

    Categoria save(Categoria categoria);

    void deleteById(Long id);
}
