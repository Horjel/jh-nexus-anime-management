package com.otakucenter.dao.impl;

import com.otakucenter.dao.ProductoDao;
import com.otakucenter.model.Producto;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductoDaoImpl implements ProductoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Producto> findAll() {
        return entityManager.createQuery(
                "select p from Producto p join fetch p.categoria order by p.nombre",
                Producto.class
        ).getResultList();
    }

    @Override
    public long countAll() {
        return entityManager.createQuery("select count(p) from Producto p", Long.class)
                .getSingleResult();
    }

    @Override
    public long countByFiltros(String termino, boolean soloStockBajo) {
        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase();
        StringBuilder jpql = new StringBuilder(
                "select count(p) from Producto p join p.categoria c where 1=1"
        );

        if (!terminoNormalizado.isEmpty()) {
            jpql.append(" and (lower(p.nombre) like :termino or lower(p.descripcion) like :termino or lower(c.nombre) like :termino)");
        }
        if (soloStockBajo) {
            jpql.append(" and p.stock <= 5");
        }

        javax.persistence.TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        if (!terminoNormalizado.isEmpty()) {
            query.setParameter("termino", "%" + terminoNormalizado + "%");
        }
        return query.getSingleResult();
    }

    @Override
    public List<Producto> findPageByFiltros(String termino, boolean soloStockBajo, String orden, int offset, int limit) {
        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase();
        StringBuilder jpql = new StringBuilder(
                "select p from Producto p join fetch p.categoria c where 1=1"
        );

        if (!terminoNormalizado.isEmpty()) {
            jpql.append(" and (lower(p.nombre) like :termino or lower(p.descripcion) like :termino or lower(c.nombre) like :termino)");
        }
        if (soloStockBajo) {
            jpql.append(" and p.stock <= 5");
        }

        if ("idAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.id asc");
        } else if ("idDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.id desc");
        } else if ("nombreDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.nombre desc");
        } else if ("descripcionAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.descripcion asc, p.nombre asc");
        } else if ("descripcionDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.descripcion desc, p.nombre asc");
        } else if ("precioAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.precio asc, p.nombre asc");
        } else if ("precioDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.precio desc, p.nombre asc");
        } else if ("stockAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.stock asc, p.nombre asc");
        } else if ("stockDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by p.stock desc, p.nombre asc");
        } else if ("categoriaAsc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.nombre asc, p.nombre asc");
        } else if ("categoriaDesc".equalsIgnoreCase(orden)) {
            jpql.append(" order by c.nombre desc, p.nombre asc");
        } else {
            jpql.append(" order by p.nombre asc");
        }

        javax.persistence.TypedQuery<Producto> query = entityManager.createQuery(jpql.toString(), Producto.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        if (!terminoNormalizado.isEmpty()) {
            query.setParameter("termino", "%" + terminoNormalizado + "%");
        }
        return query.getResultList();
    }

    @Override
    public List<Producto> findByFiltros(String termino, boolean soloStockBajo) {
        StringBuilder jpql = new StringBuilder(
                "select p from Producto p join fetch p.categoria where 1=1 "
        );

        if (!termino.isEmpty()) {
            jpql.append("and (lower(p.nombre) like :termino or lower(p.descripcion) like :termino or lower(p.categoria.nombre) like :termino) ");
        }
        if (soloStockBajo) {
            jpql.append("and p.stock <= 5 ");
        }
        jpql.append("order by p.nombre");

        javax.persistence.TypedQuery<Producto> query = entityManager.createQuery(jpql.toString(), Producto.class);
        if (!termino.isEmpty()) {
            query.setParameter("termino", "%" + termino.toLowerCase() + "%");
        }
        return query.getResultList();
    }

    @Override
    public List<Producto> findByCategoriaId(Long categoriaId) {
        return entityManager.createQuery(
                        "select p from Producto p join fetch p.categoria where p.categoria.id = :categoriaId order by p.nombre",
                        Producto.class
                )
                .setParameter("categoriaId", categoriaId)
                .getResultList();
    }

    @Override
    public List<Producto> findLowStock(int maxStock, int limit) {
        return entityManager.createQuery(
                        "select p from Producto p join fetch p.categoria where p.stock <= :maxStock order by p.stock asc, p.nombre asc",
                        Producto.class
                )
                .setParameter("maxStock", maxStock)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        List<Producto> resultados = entityManager.createQuery(
                        "select p from Producto p join fetch p.categoria where p.id = :id",
                        Producto.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();

        return resultados.stream().findFirst();
    }

    @Override
    public Optional<Producto> findByIdForUpdate(Long id) {
        List<Producto> resultados = entityManager.createQuery(
                        "select p from Producto p join fetch p.categoria where p.id = :id",
                        Producto.class
                )
                .setParameter("id", id)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .getResultList();

        return resultados.stream().findFirst();
    }

    @Override
    public Optional<Producto> findByNombre(String nombre) {
        List<Producto> resultados = entityManager.createQuery(
                        "select p from Producto p where lower(p.nombre) = lower(:nombre)",
                        Producto.class
                )
                .setParameter("nombre", nombre)
                .setMaxResults(1)
                .getResultList();

        return resultados.stream().findFirst();
    }

    @Override
    public Producto save(Producto producto) {
        if (producto.getId() == null) {
            entityManager.persist(producto);
            return producto;
        }

        return entityManager.merge(producto);
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove);
    }

    @Override
    public long countByCategoriaId(Long categoriaId) {
        return entityManager.createQuery(
                        "select count(p) from Producto p where p.categoria.id = :categoriaId",
                        Long.class
                )
                .setParameter("categoriaId", categoriaId)
                .getSingleResult();
    }
}
