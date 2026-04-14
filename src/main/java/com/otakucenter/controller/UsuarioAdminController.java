package com.otakucenter.controller;

import com.otakucenter.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("bitacoraAdmin", usuarioService.listarBitacoraAdminReciente());
        return "usuarios/lista";
    }

    @PostMapping("/usuarios/crear")
    public String crear(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String rol,
            @RequestParam(defaultValue = "false") boolean activo,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String usernameActor = authentication == null ? "" : authentication.getName();
        usuarioService.crearUsuario(username, password, rol, activo, usernameActor);
        redirectAttributes.addFlashAttribute("mensajeOk", "Usuario creado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/activar/{id}")
    public String activar(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        return cambiarEstado(id, true, authentication, redirectAttributes);
    }

    @PostMapping("/usuarios/desactivar/{id}")
    public String desactivar(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        return cambiarEstado(id, false, authentication, redirectAttributes);
    }

    @PostMapping("/usuarios/rol/{id}")
    public String actualizarRol(
            @PathVariable Long id,
            @RequestParam String rol,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String usernameActor = authentication == null ? "" : authentication.getName();
        usuarioService.actualizarRol(id, rol, usernameActor);
        redirectAttributes.addFlashAttribute("mensajeOk", "Rol actualizado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/password/{id}")
    public String actualizarPassword(
            @PathVariable Long id,
            @RequestParam String nuevaPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String usernameActor = authentication == null ? "" : authentication.getName();
        usuarioService.actualizarPassword(id, nuevaPassword, usernameActor);
        redirectAttributes.addFlashAttribute("mensajeOk", "Contrasena actualizada correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String usernameActor = authentication == null ? "" : authentication.getName();
        usuarioService.eliminarUsuario(id, usernameActor);
        redirectAttributes.addFlashAttribute("mensajeOk", "Usuario eliminado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/bitacora/limpiar")
    public String limpiarBitacora(
            @RequestParam String usernameConfirmacion,
            @RequestParam String passwordConfirmacion,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String usernameActor = authentication == null ? "" : authentication.getName();
        usuarioService.limpiarBitacoraAdmin(usernameConfirmacion, passwordConfirmacion, usernameActor);
        redirectAttributes.addFlashAttribute("mensajeOk", "Bitacora administrativa limpiada correctamente");
        return "redirect:/usuarios";
    }

    private String cambiarEstado(
            Long id,
            boolean activo,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String usernameActor = authentication == null ? "" : authentication.getName();
        usuarioService.actualizarEstado(id, activo, usernameActor);
        redirectAttributes.addFlashAttribute(
                "mensajeOk",
                activo ? "Usuario activado correctamente" : "Usuario bloqueado correctamente"
        );
        return "redirect:/usuarios";
    }
}
