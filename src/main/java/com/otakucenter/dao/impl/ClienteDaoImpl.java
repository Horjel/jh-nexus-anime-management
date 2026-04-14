package com.otakucenter.dao.impl;

import com.otakucenter.dao.ClienteDao;
import com.otakucenter.model.Cliente;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteDaoImpl implements ClienteDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Cliente> findAll() {
        return entityManager.createQuery(
                "select c from Cliente c order by c.apellidos, c.nombre",
                Cliente.class
        ).getResultList();
    }

    @Override
    public long countAll() {
        return entityManager.createQuery("select count(c) from Cliente c", Long.class)
                .getSingleResult();
    }

    @Override
    public long countByTerminoContaining(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return countAll();
        }

        return entityManager.createQuery(
                        "select count(c) from Cliente c " +
                                "where lower(c.nombre) like :termino " +
                                "or lower(c.apellidos) like :termino " +
                                "or lower(c.email) like :termino " +
                                "or lower(c.telefono) like :termino",
                        Long.class
                )
                .setParameter("termino", "%" + termino.toLowerCase() + "%")
                .getSingleResult();
    }

    @Override
    public List<Cliente> findPageByTermino(String termino, String orden, int offset, int limit) {
        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase();
        boolean filtrar = !terminoNormalizado.isEmpty();
        StringBuilder jpql = new StringBuilder("select c from Cliente c");
        if (filtrar) {
            jpql.append(" where lower(c.nombre) like :termino")
                    .append(" or lower(c.apellidos) like :termino")
                    .append(" or lower(c.email) like :termino")
                    .append(" or lower(c.telefono) like :termino");
        }
        if ("idAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.id asc");
        } else if ("idDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.id desc");
        } else if ("nombreDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.nombre desc, c.apellidos desc");
        } else if ("apellidosAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.apellidos asc, c.nombre asc");
        } else if ("apellidosDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.apellidos desc, c.nombre desc");
        } else if ("emailAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.email asc");
        } else if ("emailDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.email desc");
        } else if ("telefonoAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.telefono asc");
        } else if ("telefonoDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.telefono desc");
        } else if ("direccionAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.direccion asc");
        } else if ("direccionDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.direccion desc");
        } else {
            jpql.append(" order by c.nombre asc, c.apellidos asc");
        }

        javax.persistence.TypedQuery<Cliente> query = entityManager.createQuery(jpql.toString(), Cliente.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        if (filtrar) {
            query.setParameter("termino", "%" + terminoNormalizado + "%");
        }
        return query.getResultList();
    }

    @Override
    public List<Cliente> findByTermino(String termino) {
        return entityManager.createQuery(
                        "select c from Cliente c " +
                                "where lower(c.nombre) like :termino " +
                                "or lower(c.apellidos) like :termino " +
                                "or lower(c.email) like :termino " +
                                "or lower(c.telefono) like :termino " +
                                "order by c.apellidos, c.nombre",
                        Cliente.class
                )
                .setParameter("termino", "%" + termino.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Cliente.class, id));
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        List<Cliente> resultados = entityManager.createQuery(
                        "select c from Cliente c where lower(c.email) = lower(:email)",
                        Cliente.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();

        return resultados.stream().findFirst();
    }

    @Override
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            entityManager.persist(cliente);
            return cliente;
        }

        return entityManager.merge(cliente);
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
