package com.otakucenter.dao.impl;

import com.otakucenter.dao.BitacoraAdminDao;
import com.otakucenter.model.BitacoraAdmin;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class BitacoraAdminDaoImpl implements BitacoraAdminDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BitacoraAdmin save(BitacoraAdmin bitacoraAdmin) {
        if (bitacoraAdmin.getId() == null) {
            entityManager.persist(bitacoraAdmin);
            return bitacoraAdmin;
        }
        return entityManager.merge(bitacoraAdmin);
    }

    @Override
    public List<BitacoraAdmin> findRecent(int maxResultados) {
        return entityManager.createQuery(
                        "select b from BitacoraAdmin b order by b.fechaAccion desc, b.id desc",
                        BitacoraAdmin.class
                )
                .setMaxResults(maxResultados)
                .getResultList();
    }

    @Override
    public void deleteAll() {
        entityManager.createQuery("delete from BitacoraAdmin").executeUpdate();
    }
}
