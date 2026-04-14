package com.otakucenter.dao;

import com.otakucenter.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDao {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findById(Long id);

    List<Usuario> findAll();

    long countAll();

    Usuario save(Usuario usuario);

    void delete(Usuario usuario);
}
