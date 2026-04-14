package com.otakucenter.dao.impl;

import com.otakucenter.dao.CategoriaDao;
import com.otakucenter.model.Categoria;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoriaDaoImpl implements CategoriaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Categoria> findAll() {
        return entityManager.createQuery(
                "select c from Categoria c order by c.nombre",
                Categoria.class
        ).getResultList();
    }

    @Override
    public long countAll() {
        return entityManager.createQuery("select count(c) from Categoria c", Long.class)
                .getSingleResult();
    }

    @Override
    public long countByNombreContaining(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return countAll();
        }

        return entityManager.createQuery(
                        "select count(c) from Categoria c where lower(c.nombre) like :termino",
                        Long.class
                )
                .setParameter("termino", "%" + termino.toLowerCase() + "%")
                .getSingleResult();
    }

    @Override
    public List<Categoria> findPageByNombreContaining(String termino, String orden, int offset, int limit) {
        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase();
        StringBuilder jpql = new StringBuilder("select c from Categoria c");
        boolean filtrar = !terminoNormalizado.isEmpty();
        if (filtrar) {
            jpql.append(" where lower(c.nombre) like :termino");
        }
        if ("idAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.id asc");
        } else if ("idDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.id desc");
        } else if ("nombreDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.nombre desc");
        } else {
            jpql.append(" order by c.nombre asc");
        }

        javax.persistence.TypedQuery<Categoria> query = entityManager.createQuery(jpql.toString(), Categoria.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        if (filtrar) {
            query.setParameter("termino", "%" + terminoNormalizado + "%");
        }
        return query.getResultList();
    }

    @Override
    public List<Categoria> findByNombreContaining(String termino) {
        return entityManager.createQuery(
                        "select c from Categoria c where lower(c.nombre) like :termino order by c.nombre",
                        Categoria.class
                )
                .setParameter("termino", "%" + termino.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Categoria.class, id));
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        List<Categoria> resultados = entityManager.createQuery(
                        "select c from Categoria c where lower(c.nombre) = lower(:nombre)",
                        Categoria.class
                )
                .setParameter("nombre", nombre)
                .setMaxResults(1)
                .getResultList();

        return resultados.stream().findFirst();
    }

    @Override
    public Categoria save(Categoria categoria) {
        if (categoria.getId() == null) {
            entityManager.persist(categoria);
            return categoria;
        }

        return entityManager.merge(categoria);
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
