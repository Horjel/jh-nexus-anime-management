package com.otakucenter.controller;

import com.otakucenter.model.Categoria;
import com.otakucenter.model.PageResult;
import com.otakucenter.model.Producto;
import com.otakucenter.service.CategoriaService;
import com.otakucenter.service.ProductoService;
import com.otakucenter.util.CsvUtils;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    public CategoriaController(CategoriaService categoriaService, ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @GetMapping("/categorias")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "nombreAsc") String orden,
            @RequestParam(defaultValue = "1") int pagina,
            Model model
    ) {
        PageResult<Categoria> resultado = categoriaService.buscarPorTermino(q, orden, pagina);
        model.addAttribute("categorias", resultado.getItems());
        model.addAttribute("q", q);
        model.addAttribute("orden", orden);
        cargarPaginacion(model, resultado);
        return "categorias/lista";
    }

    @GetMapping("/categorias/nueva")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("modoEdicion", false);
        return "categorias/formulario";
    }

    @GetMapping("/categorias/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id));
        model.addAttribute("modoEdicion", true);
        return "categorias/formulario";
    }

    @GetMapping("/categorias/ver/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id);
        List<Producto> productos = productoService.listarPorCategoria(id);
        model.addAttribute("categoria", categoria);
        model.addAttribute("productosCategoria", productos);
        model.addAttribute("totalProductosCategoria", productos.size());
        return "categorias/detalle";
    }

    @GetMapping("/categorias/exportar")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "nombreAsc") String orden
    ) {
        List<Categoria> categorias = categoriaService.listarParaExportacion(q, orden);
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Nombre\n");
        for (Categoria categoria : categorias) {
            csv.append(categoria.getId()).append(";")
                    .append(CsvUtils.escape(categoria.getNombre()))
                    .append("\n");
        }
        return CsvUtils.buildResponse("categorias.csv", csv.toString());
    }

    @PostMapping("/categorias/guardar")
    public String guardar(
            @Valid @ModelAttribute("categoria") Categoria categoria,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        boolean modoEdicion = categoria.getId() != null;
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicion", modoEdicion);
            return "categorias/formulario";
        }

        try {
            String usernameActor = authentication == null ? "" : authentication.getName();
            categoriaService.guardar(categoria, usernameActor);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicion", modoEdicion);
            model.addAttribute("errorGeneral", ex.getMessage());
            return "categorias/formulario";
        }

        redirectAttributes.addFlashAttribute("mensajeOk", "Categoria guardada correctamente");
        return "redirect:/categorias";
    }

    @PostMapping("/categorias/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoriaService.eliminarPorId(id);
        redirectAttributes.addFlashAttribute("mensajeOk", "Categoria eliminada correctamente");
        return "redirect:/categorias";
    }

    private void cargarPaginacion(Model model, PageResult<?> resultado) {
        model.addAttribute("totalResultados", resultado.getTotalItems());
        model.addAttribute("paginaActual", resultado.getPage());
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("hayAnterior", resultado.isHasPrevious());
        model.addAttribute("haySiguiente", resultado.isHasNext());
        model.addAttribute("paginaAnterior", resultado.getPreviousPage());
        model.addAttribute("paginaSiguiente", resultado.getNextPage());
        model.addAttribute("inicioResultados", resultado.getStartItem());
        model.addAttribute("finResultados", resultado.getEndItem());
    }
}
