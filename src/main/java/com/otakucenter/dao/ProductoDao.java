package com.otakucenter.dao;

import com.otakucenter.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoDao {

    List<Producto> findAll();

    long countAll();

    long countByFiltros(String termino, boolean soloStockBajo);

    List<Producto> findPageByFiltros(String termino, boolean soloStockBajo, String orden, int offset, int limit);

    List<Producto> findByFiltros(String termino, boolean soloStockBajo);

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findLowStock(int maxStock, int limit);

    Optional<Producto> findById(Long id);

    Optional<Producto> findByIdForUpdate(Long id);

    Optional<Producto> findByNombre(String nombre);

    Producto save(Producto producto);

    void deleteById(Long id);

    long countByCategoriaId(Long categoriaId);
}
