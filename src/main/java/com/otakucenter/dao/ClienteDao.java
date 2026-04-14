package com.otakucenter.dao;

import com.otakucenter.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteDao {

    List<Cliente> findAll();

    long countAll();

    long countByTerminoContaining(String termino);

    List<Cliente> findPageByTermino(String termino, String orden, int offset, int limit);

    List<Cliente> findByTermino(String termino);

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByEmail(String email);

    Cliente save(Cliente cliente);

    void deleteById(Long id);
}
