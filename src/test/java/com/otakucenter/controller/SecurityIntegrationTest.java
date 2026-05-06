package com.otakucenter.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.otakucenter.config.GlobalViewExceptionHandler;
import com.otakucenter.config.SecurityBeansConfig;
import com.otakucenter.config.SecurityConfig;
import com.otakucenter.model.BitacoraAdmin;
import com.otakucenter.model.Usuario;
import com.otakucenter.service.UsuarioService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {AuthController.class, UsuarioAdminController.class})
@Import({SecurityConfig.class, SecurityBeansConfig.class, GlobalViewExceptionHandler.class})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void deberiaPermitirLoginCorrectoYRedirigirAHome() throws Exception {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("*****"))
                .roles("ADMIN")
                .build();

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(admin);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "*****"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(usuarioService).registrarAccesoExitoso("admin");
    }

    @Test
    void deberiaRedirigirALoginBlockedSiLaCuentaEstaDeshabilitada() throws Exception {
        UserDetails bloqueado = User.withUsername("Sergio Delegado")
                .password(passwordEncoder.encode("*****"))
                .roles("DELEGADO")
                .disabled(true)
                .build();

        when(userDetailsService.loadUserByUsername("Sergio Delegado")).thenReturn(bloqueado);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "Sergio Delegado")
                        .param("password", "*****"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?blocked"));

        verify(usuarioService, never()).registrarAccesoExitoso(anyString());
    }

    @Test
    void deberiaDenegarAccesoAUsuariosParaRolUser() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(user("joyux").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaPermitirAccesoAUsuariosParaRolAdmin() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(Collections.<Usuario>emptyList());
        when(usuarioService.listarBitacoraAdminReciente()).thenReturn(Collections.<BitacoraAdmin>emptyList());

        mockMvc.perform(get("/usuarios")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"));
    }
}
