package com.otakucenter.service.impl;

import com.otakucenter.dao.CategoriaDao;
import com.otakucenter.dao.ProductoDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ResourceNotFoundException;
import com.otakucenter.model.Categoria;
import com.otakucenter.model.PageResult;
import com.otakucenter.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private static final int PAGE_SIZE = 5;

    private final CategoriaDao categoriaDao;
    private final ProductoDao productoDao;

    public CategoriaServiceImpl(CategoriaDao categoriaDao, ProductoDao productoDao) {
        this.categoriaDao = categoriaDao;
        this.productoDao = productoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarCategorias() {
        return categoriaDao.countAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Categoria> buscarPorTermino(String termino, String orden, int pagina) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        int totalItems = (int) categoriaDao.countByNombreContaining(terminoNormalizado);
        int paginaNormalizada = normalizarPagina(pagina, totalItems);
        int offset = (paginaNormalizada - 1) * PAGE_SIZE;
        List<Categoria> categorias = categoriaDao.findPageByNombreContaining(terminoNormalizado, orden, offset, PAGE_SIZE);
        return PageResult.fromPage(categorias, paginaNormalizada, PAGE_SIZE, totalItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarParaExportacion(String termino, String orden) {
        String terminoNormalizado = termino == null ? "" : termino.trim();
        int totalItems = (int) categoriaDao.countByNombreContaining(terminoNormalizado);
        return categoriaDao.findPageByNombreContaining(terminoNormalizado, orden, 0, totalItems == 0 ? Integer.MAX_VALUE : totalItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con id " + id));
    }

    @Override
    public Categoria guardar(Categoria categoria, String usernameActor) {
        String nombreNormalizado = categoria.getNombre() == null ? "" : categoria.getNombre().trim();
        categoria.setNombre(nombreNormalizado);

        Optional<Categoria> categoriaExistente = categoriaDao.findByNombre(nombreNormalizado);
        if (categoriaExistente.isPresent() && !categoriaExistente.get().getId().equals(categoria.getId())) {
            throw new BusinessRuleException("Ya existe una categoria con ese nombre");
        }

        aplicarAuditoria(categoria, usernameActor);
        return categoriaDao.save(categoria);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (productoDao.countByCategoriaId(id) > 0) {
            throw new BusinessRuleException("No puedes eliminar una categoria con productos asociados");
        }
        categoriaDao.deleteById(id);
    }

    private void aplicarAuditoria(Categoria categoria, String usernameActor) {
        LocalDateTime ahora = LocalDateTime.now();
        String actor = usernameActor == null || usernameActor.trim().isEmpty() ? "SYSTEM" : usernameActor.trim();
        if (categoria.getFechaCreacion() == null) {
            categoria.setFechaCreacion(ahora);
        }
        if (categoria.getCreadoPor() == null || categoria.getCreadoPor().trim().isEmpty()) {
            categoria.setCreadoPor(actor);
        }
        categoria.setFechaActualizacion(ahora);
        categoria.setActualizadoPor(actor);
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
