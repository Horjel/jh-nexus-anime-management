package com.otakucenter.dao.impl;

import com.otakucenter.dao.PedidoDao;
import com.otakucenter.model.DashboardProductoVenta;
import com.otakucenter.model.Pedido;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PedidoDaoImpl implements PedidoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Pedido> findAll() {
        return entityManager.createQuery(
                "select distinct p from Pedido p " +
                        "join fetch p.cliente " +
                        "left join fetch p.detalles d " +
                        "left join fetch d.producto " +
                        "left join fetch p.productoLegacy " +
                        "order by p.fechaPedido desc",
                Pedido.class
        ).getResultList();
    }

    @Override
    public long countAll() {
        return entityManager.createQuery("select count(p) from Pedido p", Long.class)
                .getSingleResult();
    }

    @Override
    public BigDecimal sumTotalVentas() {
        BigDecimal total = entityManager.createQuery(
                        "select coalesce(sum(p.total), 0) from Pedido p",
                        BigDecimal.class
                )
                .getSingleResult();
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public long countByEstado(String estado) {
        return entityManager.createQuery(
                        "select count(p) from Pedido p where p.estado = :estado",
                        Long.class
                )
                .setParameter("estado", estado)
                .getSingleResult();
    }

    @Override
    public long countByFiltros(String termino, String estado) {
        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase();
        String estadoNormalizado = estado == null ? "" : estado.trim();
        StringBuilder jpql = new StringBuilder(
                "select count(distinct p.id) from Pedido p " +
                        "join p.cliente c " +
                        "left join p.detalles d " +
                        "left join d.producto dp " +
                        "left join p.productoLegacy pl " +
                        "where 1=1 "
        );

        if (!terminoNormalizado.isEmpty()) {
            jpql.append(
                    "and (" +
                            "lower(c.nombre) like :termino " +
                            "or lower(c.apellidos) like :termino " +
                            "or lower(c.email) like :termino " +
                            "or lower(dp.nombre) like :termino " +
                            "or lower(pl.nombre) like :termino" +
                            ") "
            );
        }

        if (!estadoNormalizado.isEmpty()) {
            jpql.append("and p.estado = :estado ");
        }

        javax.persistence.TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        if (!terminoNormalizado.isEmpty()) {
            query.setParameter("termino", "%" + terminoNormalizado + "%");
        }
        if (!estadoNormalizado.isEmpty()) {
            query.setParameter("estado", estadoNormalizado);
        }
        return query.getSingleResult();
    }

    @Override
    public List<Pedido> findPageByFiltros(String termino, String estado, String orden, int offset, int limit) {
        if ("detalleAsc".equalsIgnoreCase(orden)
                || "detalleDesc".equalsIgnoreCase(orden)
                || "unidadesAsc".equalsIgnoreCase(orden)
                || "unidadesDesc".equalsIgnoreCase(orden)) {
            return findPageByFiltrosConOrdenAgregado(termino, estado, orden, offset, limit);
        }

        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase();
        String estadoNormalizado = estado == null ? "" : estado.trim();
        StringBuilder jpql = new StringBuilder(
                "select distinct p from Pedido p " +
                        "join fetch p.cliente c " +
                        "left join fetch p.detalles d " +
                        "left join fetch d.producto dp " +
                        "left join fetch p.productoLegacy pl " +
                        "where 1=1 "
        );

        if (!terminoNormalizado.isEmpty()) {
            jpql.append(
                    "and (" +
                            "lower(c.nombre) like :termino " +
                            "or lower(c.apellidos) like :termino " +
                            "or lower(c.email) like :termino " +
                            "or lower(dp.nombre) like :termino " +
                            "or lower(pl.nombre) like :termino" +
                            ") "
            );
        }

        if (!estadoNormalizado.isEmpty()) {
            jpql.append("and p.estado = :estado ");
        }

        if ("idAsc".equalsIgnoreCase(orden)) {
            jpql.append("order by p.id asc");
        } else if ("idDesc".equalsIgnoreCase(orden)) {
            jpql.append("order by p.id desc");
        } else if ("fechaAsc".equalsIgnoreCase(orden)) {
            jpql.append("order by p.fechaPedido asc");
        } else if ("clienteAsc".equalsIgnoreCase(orden)) {
            jpql.append("order by c.apellidos asc, c.nombre asc, p.fechaPedido desc");
        } else if ("clienteDesc".equalsIgnoreCase(orden)) {
            jpql.append("order by c.apellidos desc, c.nombre desc, p.fechaPedido desc");
        } else if ("estadoAsc".equalsIgnoreCase(orden)) {
            jpql.append("order by p.estado asc, p.fechaPedido desc");
        } else if ("estadoDesc".equalsIgnoreCase(orden)) {
            jpql.append("order by p.estado desc, p.fechaPedido desc");
        } else if ("totalAsc".equalsIgnoreCase(orden)) {
            jpql.append("order by p.total asc, p.fechaPedido desc");
        } else if ("totalDesc".equalsIgnoreCase(orden)) {
            jpql.append("order by p.total desc, p.fechaPedido desc");
        } else {
            jpql.append("order by p.fechaPedido desc");
        }

        javax.persistence.TypedQuery<Pedido> query = entityManager.createQuery(jpql.toString(), Pedido.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        if (!terminoNormalizado.isEmpty()) {
            query.setParameter("termino", "%" + terminoNormalizado + "%");
        }
        if (!estadoNormalizado.isEmpty()) {
            query.setParameter("estado", estadoNormalizado);
        }
        return query.getResultList();
    }

    private List<Pedido> findPageByFiltrosConOrdenAgregado(String termino, String estado, String orden, int offset, int limit) {
        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase();
        String estadoNormalizado = estado == null ? "" : estado.trim();
        StringBuilder jpqlIds = new StringBuilder(
                "select p.id from Pedido p " +
                        "join p.cliente c " +
                        "left join p.detalles d " +
                        "left join d.producto dp " +
                        "left join p.productoLegacy pl " +
                        "where 1=1 "
        );

        if (!terminoNormalizado.isEmpty()) {
            jpqlIds.append(
                    "and (" +
                            "lower(c.nombre) like :termino " +
                            "or lower(c.apellidos) like :termino " +
                            "or lower(c.email) like :termino " +
                            "or lower(dp.nombre) like :termino " +
                            "or lower(pl.nombre) like :termino" +
                            ") "
            );
        }

        if (!estadoNormalizado.isEmpty()) {
            jpqlIds.append("and p.estado = :estado ");
        }

        jpqlIds.append(
                "group by p.id, pl.nombre, p.cantidadLegacy "
        );

        if ("detalleDesc".equalsIgnoreCase(orden)) {
            jpqlIds.append("order by coalesce(min(dp.nombre), pl.nombre, '') desc, p.id desc");
        } else if ("unidadesAsc".equalsIgnoreCase(orden)) {
            jpqlIds.append("order by coalesce(sum(d.cantidad), p.cantidadLegacy, 0) asc, p.id asc");
        } else if ("unidadesDesc".equalsIgnoreCase(orden)) {
            jpqlIds.append("order by coalesce(sum(d.cantidad), p.cantidadLegacy, 0) desc, p.id desc");
        } else {
            jpqlIds.append("order by coalesce(min(dp.nombre), pl.nombre, '') asc, p.id asc");
        }

        javax.persistence.TypedQuery<Long> idsQuery = entityManager.createQuery(jpqlIds.toString(), Long.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        if (!terminoNormalizado.isEmpty()) {
            idsQuery.setParameter("termino", "%" + terminoNormalizado + "%");
        }
        if (!estadoNormalizado.isEmpty()) {
            idsQuery.setParameter("estado", estadoNormalizado);
        }

        List<Long> ids = idsQuery.getResultList();
        if (ids.isEmpty()) {
            return new ArrayList<Pedido>();
        }

        List<Pedido> pedidos = entityManager.createQuery(
                        "select distinct p from Pedido p " +
                                "join fetch p.cliente " +
                                "left join fetch p.detalles d " +
                                "left join fetch d.producto " +
                                "left join fetch p.productoLegacy " +
                                "where p.id in :ids",
                        Pedido.class
                )
                .setParameter("ids", ids)
                .getResultList();

        Map<Long, Pedido> pedidosPorId = new HashMap<Long, Pedido>();
        for (Pedido pedido : pedidos) {
            pedidosPorId.put(pedido.getId(), pedido);
        }

        List<Pedido> ordenados = new ArrayList<Pedido>();
        for (Long id : ids) {
            Pedido pedido = pedidosPorId.get(id);
            if (pedido != null) {
                ordenados.add(pedido);
            }
        }
        return ordenados;
    }

    @Override
    public List<Pedido> findRecent(int limit) {
        return entityManager.createQuery(
                        "select distinct p from Pedido p " +
                                "join fetch p.cliente " +
                                "left join fetch p.detalles d " +
                                "left join fetch d.producto " +
                                "left join fetch p.productoLegacy " +
                                "order by p.fechaPedido desc",
                        Pedido.class
                )
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<DashboardProductoVenta> findTopProductosVendidos(int limit) {
        return entityManager.createQuery(
                        "select new com.otakucenter.model.DashboardProductoVenta(d.producto, sum(d.cantidad)) " +
                                "from PedidoDetalle d " +
                                "group by d.producto " +
                                "order by sum(d.cantidad) desc",
                        DashboardProductoVenta.class
                )
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Pedido> findByFiltros(String termino, String estado) {
        StringBuilder jpql = new StringBuilder(
                "select distinct p from Pedido p " +
                        "join fetch p.cliente " +
                        "left join fetch p.detalles d " +
                        "left join fetch d.producto " +
                        "left join fetch p.productoLegacy " +
                        "where 1=1 "
        );

        if (!termino.isEmpty()) {
            jpql.append(
                    "and (" +
                            "lower(p.cliente.nombre) like :termino " +
                            "or lower(p.cliente.apellidos) like :termino " +
                            "or lower(p.cliente.email) like :termino " +
                            "or lower(d.producto.nombre) like :termino " +
                            "or lower(p.productoLegacy.nombre) like :termino" +
                            ") "
            );
        }

        if (!estado.isEmpty()) {
            jpql.append("and p.estado = :estado ");
        }

        jpql.append("order by p.fechaPedido desc");

        javax.persistence.TypedQuery<Pedido> query = entityManager.createQuery(jpql.toString(), Pedido.class);
        if (!termino.isEmpty()) {
            query.setParameter("termino", "%" + termino.toLowerCase() + "%");
        }
        if (!estado.isEmpty()) {
            query.setParameter("estado", estado);
        }
        return query.getResultList();
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        List<Pedido> resultados = entityManager.createQuery(
                        "select distinct p from Pedido p " +
                                "join fetch p.cliente " +
                                "left join fetch p.detalles d " +
                                "left join fetch d.producto " +
                                "left join fetch p.productoLegacy " +
                                "where p.id = :id",
                        Pedido.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();

        return resultados.stream().findFirst();
    }

    @Override
    public Pedido save(Pedido pedido) {
        if (pedido.getId() == null) {
            entityManager.persist(pedido);
            return pedido;
        }
        return entityManager.merge(pedido);
    }

    @Override
    public void delete(Pedido pedido) {
        entityManager.remove(entityManager.contains(pedido) ? pedido : entityManager.merge(pedido));
    }

    @Override
    public long countByClienteId(Long clienteId) {
        return entityManager.createQuery(
                        "select count(p) from Pedido p where p.cliente.id = :clienteId",
                        Long.class
                )
                .setParameter("clienteId", clienteId)
                .getSingleResult();
    }

    @Override
    public long countByProductoId(Long productoId) {
        Long detalles = entityManager.createQuery(
                        "select count(d) from PedidoDetalle d where d.producto.id = :productoId",
                        Long.class
                )
                .setParameter("productoId", productoId)
                .getSingleResult();

        Long legacy = entityManager.createQuery(
                        "select count(p) from Pedido p where p.productoLegacy.id = :productoId and p.id not in " +
                                "(select distinct pd.pedido.id from PedidoDetalle pd)",
                        Long.class
                )
                .setParameter("productoId", productoId)
                .getSingleResult();

        return detalles + legacy;
    }
}
