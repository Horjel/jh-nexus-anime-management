package com.otakucenter.controller;

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
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/productos")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "false") boolean stockBajo,
            @RequestParam(defaultValue = "nombreAsc") String orden,
            @RequestParam(defaultValue = "1") int pagina,
            Model model
    ) {
        PageResult<Producto> resultado = productoService.buscarPorFiltros(q, stockBajo, orden, pagina);
        model.addAttribute("productos", resultado.getItems());
        model.addAttribute("q", q);
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("orden", orden);
        cargarPaginacion(model, resultado);
        return "productos/lista";
    }

    @GetMapping("/productos/nuevo")
    public String mostrarFormularioAlta(Model model) {
        prepararFormulario(model, new Producto(), false);
        return "productos/formulario";
    }

    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        prepararFormulario(model, productoService.buscarPorId(id), true);
        return "productos/formulario";
    }

    @GetMapping("/productos/ver/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        return "productos/detalle";
    }

    @GetMapping("/productos/exportar")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "false") boolean stockBajo,
            @RequestParam(defaultValue = "nombreAsc") String orden
    ) {
        List<Producto> productos = productoService.listarParaExportacion(q, stockBajo, orden);
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Nombre;Descripcion;Precio;Stock;Categoria\n");
        for (Producto producto : productos) {
            csv.append(producto.getId()).append(";")
                    .append(CsvUtils.escape(producto.getNombre())).append(";")
                    .append(CsvUtils.escape(producto.getDescripcion())).append(";")
                    .append(producto.getPrecio()).append(";")
                    .append(producto.getStock()).append(";")
                    .append(CsvUtils.escape(producto.getCategoria().getNombre()))
                    .append("\n");
        }
        return CsvUtils.buildResponse("productos.csv", csv.toString());
    }

    @PostMapping("/productos/guardar")
    public String guardar(
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        boolean modoEdicion = producto.getId() != null;
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, producto, modoEdicion);
            return "productos/formulario";
        }

        try {
            String usernameActor = authentication == null ? "" : authentication.getName();
            productoService.guardar(producto, usernameActor);
        } catch (IllegalArgumentException ex) {
            prepararFormulario(model, producto, modoEdicion);
            model.addAttribute("errorGeneral", ex.getMessage());
            return "productos/formulario";
        }

        redirectAttributes.addFlashAttribute("mensajeOk", "Producto guardado correctamente");
        return "redirect:/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.eliminarPorId(id);
        redirectAttributes.addFlashAttribute("mensajeOk", "Producto eliminado correctamente");
        return "redirect:/productos";
    }

    private void prepararFormulario(Model model, Producto producto, boolean modoEdicion) {
        model.addAttribute("producto", producto);
        model.addAttribute("modoEdicion", modoEdicion);
        model.addAttribute("categorias", categoriaService.listarTodas());
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
