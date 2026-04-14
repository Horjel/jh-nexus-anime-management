package com.otakucenter.controller;

import com.otakucenter.model.PageResult;
import com.otakucenter.model.Pedido;
import com.otakucenter.model.PedidoForm;
import com.otakucenter.service.ClienteService;
import com.otakucenter.service.PedidoService;
import com.otakucenter.service.ProductoService;
import com.otakucenter.util.CsvUtils;
import java.util.List;
import javax.validation.Valid;
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

@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public PedidoController(PedidoService pedidoService, ClienteService clienteService, ProductoService productoService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping("/pedidos")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "fechaDesc") String orden,
            @RequestParam(defaultValue = "1") int pagina,
            Model model
    ) {
        PageResult<com.otakucenter.model.Pedido> resultado = pedidoService.buscarPorFiltros(q, estado, orden, pagina);
        model.addAttribute("pedidos", resultado.getItems());
        model.addAttribute("q", q);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("orden", orden);
        cargarPaginacion(model, resultado);
        return "pedidos/lista";
    }

    @GetMapping("/pedidos/nuevo")
    public String mostrarFormularioAlta(Model model) {
        prepararFormulario(model, pedidoService.crearFormulario(), false);
        return "pedidos/formulario";
    }

    @GetMapping("/pedidos/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        prepararFormulario(model, pedidoService.crearFormularioDesdePedido(id), true);
        return "pedidos/formulario";
    }

    @GetMapping("/pedidos/ver/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscarPorId(id));
        return "pedidos/detalle";
    }

    @GetMapping("/pedidos/exportar")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "fechaDesc") String orden
    ) {
        List<Pedido> pedidos = pedidoService.listarParaExportacion(q, estado, orden);
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Fecha;Cliente;Email;Detalle;Unidades;Estado;Total\n");
        for (Pedido pedido : pedidos) {
            csv.append(pedido.getId()).append(";")
                    .append(CsvUtils.escape(pedido.getFechaPedidoTexto())).append(";")
                    .append(CsvUtils.escape(pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellidos())).append(";")
                    .append(CsvUtils.escape(pedido.getCliente().getEmail())).append(";")
                    .append(CsvUtils.escape(pedido.getResumenProductos())).append(";")
                    .append(pedido.getCantidadTotal()).append(";")
                    .append(CsvUtils.escape(pedido.getEstado())).append(";")
                    .append(pedido.getTotal())
                    .append("\n");
        }
        return CsvUtils.buildResponse("pedidos.csv", csv.toString());
    }

    @PostMapping("/pedidos/guardar")
    public String guardar(
            @Valid @ModelAttribute("pedidoForm") PedidoForm pedidoForm,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        boolean modoEdicion = pedidoForm.getId() != null;
        pedidoForm.asegurarLineasMinimas();

        if (bindingResult.hasErrors()) {
            prepararFormulario(model, pedidoForm, modoEdicion);
            return "pedidos/formulario";
        }

        try {
            String usernameActor = authentication == null ? "" : authentication.getName();
            pedidoService.guardar(pedidoForm, usernameActor);
        } catch (IllegalArgumentException ex) {
            prepararFormulario(model, pedidoForm, modoEdicion);
            model.addAttribute("errorGeneral", ex.getMessage());
            return "pedidos/formulario";
        }

        redirectAttributes.addFlashAttribute("mensajeOk", "Pedido guardado correctamente");
        return "redirect:/pedidos";
    }

    @PostMapping("/pedidos/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pedidoService.eliminarPorId(id);
        redirectAttributes.addFlashAttribute("mensajeOk", "Pedido eliminado correctamente");
        return "redirect:/pedidos";
    }

    private void prepararFormulario(Model model, PedidoForm pedidoForm, boolean modoEdicion) {
        pedidoForm.asegurarLineasMinimas();
        model.addAttribute("pedidoForm", pedidoForm);
        model.addAttribute("modoEdicion", modoEdicion);
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("estados", new String[] {"PENDIENTE", "ENVIADO", "ENTREGADO"});
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
