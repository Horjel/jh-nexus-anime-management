package com.otakucenter.dao;

import com.otakucenter.model.BitacoraAdmin;
import java.util.List;

public interface BitacoraAdminDao {

    BitacoraAdmin save(BitacoraAdmin bitacoraAdmin);

    List<BitacoraAdmin> findRecent(int maxResultados);

    void deleteAll();
}
