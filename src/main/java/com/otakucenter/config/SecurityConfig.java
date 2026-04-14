package com.otakucenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import com.otakucenter.service.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler
    ) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/login", "/assets/**").permitAll()
                        .antMatchers("/usuarios/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/categorias/nueva", "/categorias/editar/**").hasRole("ADMIN")
                        .antMatchers("/categorias/guardar").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/categorias/eliminar/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/productos/nuevo", "/productos/editar/**").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers("/productos/guardar").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers(HttpMethod.POST, "/productos/eliminar/**").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers(HttpMethod.GET, "/clientes/nuevo", "/clientes/editar/**").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers("/clientes/guardar").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers(HttpMethod.POST, "/clientes/eliminar/**").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers(HttpMethod.GET, "/pedidos/nuevo", "/pedidos/editar/**").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers("/pedidos/guardar").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers(HttpMethod.POST, "/pedidos/eliminar/**").hasAnyRole("ADMIN", "DELEGADO")
                        .antMatchers(HttpMethod.GET,
                                "/categorias/exportar",
                                "/productos/exportar",
                                "/clientes/exportar",
                                "/pedidos/exportar"
                        ).hasAnyRole("ADMIN", "DELEGADO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(UsuarioService usuarioService) {
        return (request, response, authentication) -> {
            usuarioService.registrarAccesoExitoso(authentication == null ? "" : authentication.getName());
            response.sendRedirect(request.getContextPath() + "/");
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String destino = "/login?error";
            if (esUsuarioBloqueado(exception)) {
                destino = "/login?blocked";
            }
            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    private boolean esUsuarioBloqueado(AuthenticationException exception) {
        Throwable actual = exception;
        while (actual != null) {
            if (actual instanceof DisabledException) {
                return true;
            }
            actual = actual.getCause();
        }
        return false;
    }
}
