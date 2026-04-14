package com.otakucenter.controller;

import com.otakucenter.model.Cliente;
import com.otakucenter.model.PageResult;
import com.otakucenter.service.ClienteService;
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
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/clientes")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "nombreAsc") String orden,
            @RequestParam(defaultValue = "1") int pagina,
            Model model
    ) {
        PageResult<Cliente> resultado = clienteService.buscarPorTermino(q, orden, pagina);
        model.addAttribute("clientes", resultado.getItems());
        model.addAttribute("q", q);
        model.addAttribute("orden", orden);
        cargarPaginacion(model, resultado);
        return "clientes/lista";
    }

    @GetMapping("/clientes/nuevo")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modoEdicion", false);
        return "clientes/formulario";
    }

    @GetMapping("/clientes/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        model.addAttribute("modoEdicion", true);
        return "clientes/formulario";
    }

    @GetMapping("/clientes/ver/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        return "clientes/detalle";
    }

    @GetMapping("/clientes/exportar")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "nombreAsc") String orden
    ) {
        List<Cliente> clientes = clienteService.listarParaExportacion(q, orden);
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Nombre;Apellidos;Email;Telefono;Direccion\n");
        for (Cliente cliente : clientes) {
            csv.append(cliente.getId()).append(";")
                    .append(CsvUtils.escape(cliente.getNombre())).append(";")
                    .append(CsvUtils.escape(cliente.getApellidos())).append(";")
                    .append(CsvUtils.escape(cliente.getEmail())).append(";")
                    .append(CsvUtils.escape(cliente.getTelefono())).append(";")
                    .append(CsvUtils.escape(cliente.getDireccion()))
                    .append("\n");
        }
        return CsvUtils.buildResponse("clientes.csv", csv.toString());
    }

    @PostMapping("/clientes/guardar")
    public String guardar(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        boolean modoEdicion = cliente.getId() != null;
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicion", modoEdicion);
            return "clientes/formulario";
        }

        try {
            String usernameActor = authentication == null ? "" : authentication.getName();
            clienteService.guardar(cliente, usernameActor);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicion", modoEdicion);
            model.addAttribute("errorGeneral", ex.getMessage());
            return "clientes/formulario";
        }

        redirectAttributes.addFlashAttribute("mensajeOk", "Cliente guardado correctamente");
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.eliminarPorId(id);
        redirectAttributes.addFlashAttribute("mensajeOk", "Cliente eliminado correctamente");
        return "redirect:/clientes";
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
