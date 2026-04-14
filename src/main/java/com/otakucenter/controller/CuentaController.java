package com.otakucenter.controller;

import com.otakucenter.model.Usuario;
import com.otakucenter.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CuentaController {

    private final UsuarioService usuarioService;

    public CuentaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/mi-cuenta")
    public String verMiCuenta(Authentication authentication, Model model) {
        String username = authentication == null ? "" : authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        model.addAttribute("usuario", usuario);
        return "cuenta/mi-cuenta";
    }
}
