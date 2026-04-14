package com.otakucenter.service.impl;

import com.otakucenter.dao.ClienteDao;
import com.otakucenter.dao.PedidoDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ResourceNotFoundException;
import com.otakucenter.model.Cliente;
import com.otakucenter.model.PageResult;
import com.otakucenter.service.ClienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private static final int PAGE_SIZE = 5;

    private final ClienteDao clienteDao;
    private final PedidoDao pedidoDao;

    public ClienteServiceImpl(ClienteDao clienteDao, PedidoDao pedidoDao) {
        this.clienteDao = clienteDao;
        this.pedidoDao = pedidoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarClientes() {
        return clienteDao.countAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Cliente> buscarPorTermino(String termino, String orden, int pagina) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        int totalItems = (int) clienteDao.countByTerminoContaining(terminoNormalizado);
        int paginaNormalizada = normalizarPagina(pagina, totalItems);
        int offset = (paginaNormalizada - 1) * PAGE_SIZE;
        List<Cliente> clientes = clienteDao.findPageByTermino(terminoNormalizado, orden, offset, PAGE_SIZE);
        return PageResult.fromPage(clientes, paginaNormalizada, PAGE_SIZE, totalItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarParaExportacion(String termino, String orden) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        int totalItems = (int) clienteDao.countByTerminoContaining(terminoNormalizado);
        return clienteDao.findPageByTermino(terminoNormalizado, orden, 0, totalItems == 0 ? Integer.MAX_VALUE : totalItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con id " + id));
    }

    @Override
    public Cliente guardar(Cliente cliente, String usernameActor) {
        cliente.setNombre(normalizar(cliente.getNombre()));
        cliente.setApellidos(normalizar(cliente.getApellidos()));
        cliente.setEmail(normalizar(cliente.getEmail()).toLowerCase());
        cliente.setTelefono(normalizar(cliente.getTelefono()));
        cliente.setDireccion(normalizar(cliente.getDireccion()));

        Optional<Cliente> clienteExistente = clienteDao.findByEmail(cliente.getEmail());
        if (clienteExistente.isPresent() && !clienteExistente.get().getId().equals(cliente.getId())) {
            throw new BusinessRuleException("Ya existe un cliente con ese email");
        }

        aplicarAuditoria(cliente, usernameActor);
        return clienteDao.save(cliente);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (pedidoDao.countByClienteId(id) > 0) {
            throw new BusinessRuleException("No puedes eliminar un cliente con pedidos asociados");
        }
        clienteDao.deleteById(id);
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private void aplicarAuditoria(Cliente cliente, String usernameActor) {
        LocalDateTime ahora = LocalDateTime.now();
        String actor = usernameActor == null || usernameActor.trim().isEmpty() ? "SYSTEM" : usernameActor.trim();
        if (cliente.getFechaCreacion() == null) {
            cliente.setFechaCreacion(ahora);
        }
        if (cliente.getCreadoPor() == null || cliente.getCreadoPor().trim().isEmpty()) {
            cliente.setCreadoPor(actor);
        }
        cliente.setFechaActualizacion(ahora);
        cliente.setActualizadoPor(actor);
    }

    private int normalizarPagina(int paginaSolicitada, int totalItems) {
        if (totalItems <= 0) {
            return 1;
        }
        int totalPaginas = (int) Math.ceil((double) totalItems / (double) PAGE_SIZE);
        if (paginaSolicitada < 1) {
            return 1;
        }
        return Math.min(paginaSolicitada, totalPaginas);
    }
}
