package com.otakucenter.dao.impl;

import com.otakucenter.dao.UsuarioDao;
import com.otakucenter.model.Usuario;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Usuario> findByUsername(String username) {
        List<Usuario> resultados = entityManager.createQuery(
                        "select u from Usuario u where lower(u.username) = lower(:username)",
                        Usuario.class
                )
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList();

        return resultados.stream().findFirst();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Usuario.class, id));
    }

    @Override
    public List<Usuario> findAll() {
        return entityManager.createQuery(
                        "select u from Usuario u order by lower(u.username) asc",
                        Usuario.class
                )
                .getResultList();
    }

    @Override
    public long countAll() {
        return entityManager.createQuery("select count(u) from Usuario u", Long.class)
                .getSingleResult();
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            entityManager.persist(usuario);
            return usuario;
        }
        return entityManager.merge(usuario);
    }

    @Override
    public void delete(Usuario usuario) {
        Usuario managed = entityManager.contains(usuario) ? usuario : entityManager.merge(usuario);
        entityManager.remove(managed);
    }
}
