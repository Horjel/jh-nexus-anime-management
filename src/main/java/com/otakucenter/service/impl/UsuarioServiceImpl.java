package com.otakucenter.service.impl;

import com.otakucenter.dao.BitacoraAdminDao;
import com.otakucenter.dao.UsuarioDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ForbiddenOperationException;
import com.otakucenter.exception.ResourceNotFoundException;
import com.otakucenter.model.BitacoraAdmin;
import com.otakucenter.model.Usuario;
import com.otakucenter.service.UsuarioService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private static final int LIMITE_BITACORA = 12;

    private final BitacoraAdminDao bitacoraAdminDao;
    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(
            BitacoraAdminDao bitacoraAdminDao,
            UsuarioDao usuarioDao,
            PasswordEncoder passwordEncoder
    ) {
        this.bitacoraAdminDao = bitacoraAdminDao;
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public long contarUsuarios() {
        return usuarioDao.countAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BitacoraAdmin> listarBitacoraAdminReciente() {
        return bitacoraAdminDao.findRecent(LIMITE_BITACORA);
    }

    @Override
    public void registrarAccesoExitoso(String username) {
        String usernameNormalizado = normalizar(username);
        if (usernameNormalizado.isEmpty()) {
            return;
        }

        usuarioDao.findByUsername(usernameNormalizado).ifPresent(usuario -> {
            usuario.setFechaUltimoAcceso(LocalDateTime.now());
            usuarioDao.save(usuario);
        });
    }

    @Override
    public void limpiarBitacoraAdmin(String usernameConfirmacion, String passwordConfirmacion, String usernameActor) {
        String actorNormalizado = normalizar(usernameActor);
        String confirmacionNormalizada = normalizar(usernameConfirmacion);
        String passwordNormalizada = normalizar(passwordConfirmacion);

        if (actorNormalizado.isEmpty()) {
            throw new ForbiddenOperationException("No se pudo identificar al administrador actual.");
        }

        if (!actorNormalizado.equalsIgnoreCase(confirmacionNormalizada)) {
            throw new ForbiddenOperationException("El usuario de confirmacion debe coincidir con tu sesion actual.");
        }

        if (passwordNormalizada.isEmpty()) {
            throw new BusinessRuleException("Debes indicar la contrasena actual del admin para limpiar la bitacora.");
        }

        Usuario actor = usuarioDao.findByUsername(actorNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la cuenta del administrador actual."));

        if (!actor.isActivo() || !"ADMIN".equalsIgnoreCase(normalizarRol(actor.getRol()))) {
            throw new ForbiddenOperationException("Solo una cuenta ADMIN activa puede limpiar la bitacora.");
        }

        if (!passwordEncoder.matches(passwordNormalizada, actor.getPassword())) {
            throw new ForbiddenOperationException("La contrasena de confirmacion no es valida.");
        }

        bitacoraAdminDao.deleteAll();
    }

    @Override
    public void sincronizarUsuarioProyecto(String username, String password, String rol, boolean activo) {
        String usernameNormalizado = normalizar(username);
        if (usernameNormalizado.isEmpty()) {
            throw new BusinessRuleException("El nombre de usuario del bootstrap es obligatorio.");
        }

        if (usuarioDao.findByUsername(usernameNormalizado).isPresent()) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(usernameNormalizado);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(normalizarRol(rol));
        usuario.setActivo(activo);
        aplicarAuditoriaUsuario(usuario, "SYSTEM");
        usuarioDao.save(usuario);
        registrarAccionAdmin("SYSTEM", "BOOTSTRAP_USUARIO", usuario.getUsername(), "Rol " + usuario.getRol());
    }

    @Override
    public void crearUsuario(String username, String password, String rol, boolean activo, String usernameActor) {
        String usernameNormalizado = normalizar(username);
        String passwordNormalizada = normalizar(password);
        String rolNormalizado = normalizarRol(rol);

        if (usernameNormalizado.isEmpty()) {
            throw new BusinessRuleException("El nombre de usuario es obligatorio.");
        }

        if (passwordNormalizada.length() < 4) {
            throw new BusinessRuleException("La contrasena debe tener al menos 4 caracteres.");
        }

        if (!esRolValido(rolNormalizado)) {
            throw new BusinessRuleException("Rol no valido. Usa ADMIN, DELEGADO o USER.");
        }

        if (usuarioDao.findByUsername(usernameNormalizado).isPresent()) {
            throw new BusinessRuleException("Ya existe un usuario con ese nombre.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(usernameNormalizado);
        usuario.setPassword(passwordEncoder.encode(passwordNormalizada));
        usuario.setRol(rolNormalizado);
        usuario.setActivo(activo);
        aplicarAuditoriaUsuario(usuario, usernameActor);
        usuarioDao.save(usuario);
        registrarAccionAdmin(usernameActor, "CREAR_USUARIO", usuario.getUsername(), "Rol " + usuario.getRol());
    }

    @Override
    public void actualizarEstado(Long id, boolean activo, String usernameActor) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario indicado."));

        String actorNormalizado = normalizar(usernameActor);
        if (!activo && normalizar(usuario.getUsername()).equalsIgnoreCase(actorNormalizado)) {
            throw new ForbiddenOperationException("No puedes desactivar tu propia cuenta.");
        }

        if (!activo && "ADMIN".equalsIgnoreCase(normalizarRol(usuario.getRol()))) {
            throw new ForbiddenOperationException("No se puede desactivar una cuenta ADMIN desde este panel.");
        }

        if (usuario.isActivo() == activo) {
            throw new BusinessRuleException("No se detectaron cambios de estado para guardar.");
        }

        usuario.setActivo(activo);
        aplicarAuditoriaUsuario(usuario, usernameActor);
        usuarioDao.save(usuario);
        registrarAccionAdmin(
                usernameActor,
                activo ? "DESBLOQUEAR_USUARIO" : "BLOQUEAR_USUARIO",
                usuario.getUsername(),
                "Estado " + (activo ? "ACTIVO" : "BLOQUEADO")
        );
    }

    @Override
    public void actualizarRol(Long id, String nuevoRol, String usernameActor) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario indicado."));

        String rolNormalizado = normalizarRol(nuevoRol);
        String actorNormalizado = normalizar(usernameActor);

        if (!esRolValido(rolNormalizado)) {
            throw new BusinessRuleException("Rol no valido. Usa ADMIN, DELEGADO o USER.");
        }

        if ("ADMIN".equalsIgnoreCase(normalizarRol(usuario.getRol()))) {
            throw new ForbiddenOperationException("No se puede cambiar el rol de una cuenta ADMIN desde este panel.");
        }

        if (normalizar(usuario.getUsername()).equalsIgnoreCase(actorNormalizado) && !"ADMIN".equals(rolNormalizado)) {
            throw new ForbiddenOperationException("No puedes degradar tu propia cuenta desde este panel.");
        }

        if (rolNormalizado.equalsIgnoreCase(normalizarRol(usuario.getRol()))) {
            throw new BusinessRuleException("No se detectaron cambios de rol para guardar.");
        }

        usuario.setRol(rolNormalizado);
        aplicarAuditoriaUsuario(usuario, usernameActor);
        usuarioDao.save(usuario);
        registrarAccionAdmin(usernameActor, "CAMBIAR_ROL", usuario.getUsername(), "Nuevo rol " + usuario.getRol());
    }

    @Override
    public void actualizarPassword(Long id, String nuevaPassword, String usernameActor) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario indicado."));

        String passwordNormalizada = normalizar(nuevaPassword);
        if (passwordNormalizada.length() < 4) {
            throw new BusinessRuleException("La nueva contrasena debe tener al menos 4 caracteres.");
        }

        if (passwordEncoder.matches(passwordNormalizada, usuario.getPassword())) {
            throw new BusinessRuleException("La nueva contrasena es igual a la actual. No hay cambios para guardar.");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNormalizada));
        aplicarAuditoriaUsuario(usuario, usernameActor);
        usuarioDao.save(usuario);
        registrarAccionAdmin(usernameActor, "CAMBIAR_PASSWORD", usuario.getUsername(), "Contrasena regenerada");
    }

    @Override
    public void eliminarUsuario(Long id, String usernameActor) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario indicado."));

        String actorNormalizado = normalizar(usernameActor);
        if (normalizar(usuario.getUsername()).equalsIgnoreCase(actorNormalizado)) {
            throw new ForbiddenOperationException("No puedes eliminar tu propia cuenta.");
        }

        if ("ADMIN".equalsIgnoreCase(normalizarRol(usuario.getRol()))) {
            throw new ForbiddenOperationException("No se puede eliminar una cuenta ADMIN desde este panel.");
        }

        usuarioDao.delete(usuario);
        registrarAccionAdmin(usernameActor, "ELIMINAR_USUARIO", usuario.getUsername(), "Rol previo " + usuario.getRol());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .disabled(!usuario.isActivo())
                .build();
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String normalizarRol(String rol) {
        String valor = normalizar(rol).toUpperCase();
        return valor.isEmpty() ? "USER" : valor;
    }

    private boolean esRolValido(String rol) {
        return "ADMIN".equals(rol) || "DELEGADO".equals(rol) || "USER".equals(rol);
    }

    private void aplicarAuditoriaUsuario(Usuario usuario, String usernameActor) {
        LocalDateTime ahora = LocalDateTime.now();
        String actor = normalizar(usernameActor).isEmpty() ? "SYSTEM" : normalizar(usernameActor);
        if (usuario.getFechaCreacion() == null) {
            usuario.setFechaCreacion(ahora);
        }
        if (normalizar(usuario.getCreadoPor()).isEmpty()) {
            usuario.setCreadoPor(actor);
        }
        usuario.setFechaActualizacion(ahora);
        usuario.setActualizadoPor(actor);
    }

    private void registrarAccionAdmin(String usernameActor, String accion, String objetivo, String detalle) {
        BitacoraAdmin bitacoraAdmin = new BitacoraAdmin();
        String actorNormalizado = normalizar(usernameActor);
        bitacoraAdmin.setActorUsername(actorNormalizado.isEmpty() ? "SYSTEM" : actorNormalizado);
        bitacoraAdmin.setAccion(normalizar(accion));
        bitacoraAdmin.setObjetivo(normalizar(objetivo));
        bitacoraAdmin.setDetalle(normalizar(detalle));
        bitacoraAdmin.setFechaAccion(LocalDateTime.now());
        bitacoraAdminDao.save(bitacoraAdmin);
    }
}
