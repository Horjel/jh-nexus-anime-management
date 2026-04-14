package com.otakucenter.service;

import com.otakucenter.model.Cliente;
import com.otakucenter.model.PageResult;

import java.util.List;

public interface ClienteService {

    List<Cliente> listarTodos();

    long contarClientes();

    PageResult<Cliente> buscarPorTermino(String termino, String orden, int pagina);

    List<Cliente> listarParaExportacion(String termino, String orden);

    Cliente buscarPorId(Long id);

    Cliente guardar(Cliente cliente, String usernameActor);

    void eliminarPorId(Long id);
}
