package com.otakucenter.config;

import com.otakucenter.service.UsuarioService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "app.security.bootstrap.enabled", havingValue = "true")
public class SecurityBootstrap implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final ResourceLoader resourceLoader;

    @Value("${app.security.users-file:file:./USUARIOS_PROYECTO.txt}")
    private String usersFileLocation;

    public SecurityBootstrap(UsuarioService usuarioService, ResourceLoader resourceLoader) {
        this.usuarioService = usuarioService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) {
        if (usuarioService.contarUsuarios() > 0) {
            return;
        }

        Resource resource = resourceLoader.getResource(usersFileLocation);
        if (!resource.exists()) {
            throw new IllegalStateException("No se encontro el archivo de usuarios del proyecto: " + usersFileLocation);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                procesarLinea(line, lineNumber);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer el archivo de usuarios del proyecto", ex);
        }
    }

    private void procesarLinea(String rawLine, int lineNumber) {
        String line = rawLine == null ? "" : rawLine.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }

        String[] parts = line.split(";", -1);
        if (parts.length != 4) {
            throw new IllegalStateException("Linea invalida en USUARIOS_PROYECTO.txt: " + lineNumber);
        }

        String username = parts[0].trim();
        String password = parts[1].trim();
        String rol = parts[2].trim();
        boolean activo = Boolean.parseBoolean(parts[3].trim());

        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalStateException("Usuario o password vacio en USUARIOS_PROYECTO.txt: " + lineNumber);
        }

        usuarioService.sincronizarUsuarioProyecto(username, password, rol, activo);
    }
}
