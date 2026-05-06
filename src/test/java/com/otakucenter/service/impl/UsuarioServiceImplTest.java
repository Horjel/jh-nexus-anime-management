package com.otakucenter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.otakucenter.dao.BitacoraAdminDao;
import com.otakucenter.dao.UsuarioDao;
import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ForbiddenOperationException;
import com.otakucenter.model.BitacoraAdmin;
import com.otakucenter.model.Usuario;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private BitacoraAdminDao bitacoraAdminDao;

    @Mock
    private UsuarioDao usuarioDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void deberiaCrearUsuarioValidoYRegistrarBitacora() {
        when(usuarioDao.findByUsername("nuevoUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("claveSegura")).thenReturn("hash-123");

        usuarioService.crearUsuario("nuevoUser", "claveSegura", "delegado", true, "admin");

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioDao).save(usuarioCaptor.capture());
        Usuario guardado = usuarioCaptor.getValue();

        assertEquals("nuevoUser", guardado.getUsername());
        assertEquals("hash-123", guardado.getPassword());
        assertEquals("DELEGADO", guardado.getRol());
        assertTrue(guardado.isActivo());
        assertEquals("admin", guardado.getCreadoPor());
        assertEquals("admin", guardado.getActualizadoPor());

        ArgumentCaptor<BitacoraAdmin> bitacoraCaptor = ArgumentCaptor.forClass(BitacoraAdmin.class);
        verify(bitacoraAdminDao).save(bitacoraCaptor.capture());
        assertEquals("CREAR_USUARIO", bitacoraCaptor.getValue().getAccion());
        assertEquals("nuevoUser", bitacoraCaptor.getValue().getObjetivo());
    }

    @Test
    void deberiaRechazarUsuarioDuplicado() {
        when(usuarioDao.findByUsername("admin")).thenReturn(Optional.of(crearUsuario(1L, "admin", "hash", "ADMIN", true)));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crearUsuario("admin", "claveSegura", "USER", true, "admin"));

        assertEquals("Ya existe un usuario con ese nombre.", ex.getMessage());
        verify(usuarioDao, never()).save(any(Usuario.class));
        verifyNoInteractions(bitacoraAdminDao);
    }

    @Test
    void deberiaRechazarPasswordCortaAlCrearUsuario() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crearUsuario("nuevoUser", "abc", "USER", true, "admin"));

        assertEquals("La contrasena debe tener al menos 4 caracteres.", ex.getMessage());
        verifyNoInteractions(usuarioDao, bitacoraAdminDao, passwordEncoder);
    }

    @Test
    void deberiaActualizarEstadoYRegistrarBitacora() {
        Usuario usuario = crearUsuario(5L, "joyux", "hash", "USER", true);
        when(usuarioDao.findById(5L)).thenReturn(Optional.of(usuario));

        usuarioService.actualizarEstado(5L, false, "admin");

        assertFalse(usuario.isActivo());
        verify(usuarioDao).save(usuario);
        verify(bitacoraAdminDao).save(any(BitacoraAdmin.class));
    }

    @Test
    void deberiaImpedirDesactivarTuPropiaCuenta() {
        Usuario usuario = crearUsuario(1L, "admin", "hash", "ADMIN", true);
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));

        ForbiddenOperationException ex = assertThrows(ForbiddenOperationException.class,
                () -> usuarioService.actualizarEstado(1L, false, "admin"));

        assertEquals("No puedes desactivar tu propia cuenta.", ex.getMessage());
        verify(usuarioDao, never()).save(any(Usuario.class));
        verifyNoInteractions(bitacoraAdminDao);
    }

    @Test
    void deberiaImpedirCambiarRolDeUnAdmin() {
        Usuario usuario = crearUsuario(2L, "superadmin", "hash", "ADMIN", true);
        when(usuarioDao.findById(2L)).thenReturn(Optional.of(usuario));

        ForbiddenOperationException ex = assertThrows(ForbiddenOperationException.class,
                () -> usuarioService.actualizarRol(2L, "USER", "admin"));

        assertEquals("No se puede cambiar el rol de una cuenta ADMIN desde este panel.", ex.getMessage());
        verify(usuarioDao, never()).save(any(Usuario.class));
        verifyNoInteractions(bitacoraAdminDao);
    }

    @Test
    void deberiaRechazarPasswordIgualALaActual() {
        Usuario usuario = crearUsuario(7L, "joyux", "hash-actual", "USER", true);
        when(usuarioDao.findById(7L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mismaClave", "hash-actual")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.actualizarPassword(7L, "mismaClave", "admin"));

        assertEquals("La nueva contrasena es igual a la actual. No hay cambios para guardar.", ex.getMessage());
        verify(usuarioDao, never()).save(any(Usuario.class));
        verify(bitacoraAdminDao, never()).save(any(BitacoraAdmin.class));
    }

    @Test
    void deberiaLimpiarBitacoraConConfirmacionValida() {
        Usuario admin = crearUsuario(1L, "admin", "hash-admin", "ADMIN", true);
        when(usuarioDao.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("claveActual", "hash-admin")).thenReturn(true);

        usuarioService.limpiarBitacoraAdmin("admin", "claveActual", "admin");

        verify(bitacoraAdminDao).deleteAll();
    }

    @Test
    void deberiaRechazarLimpiezaBitacoraSiLaContrasenaNoCoincide() {
        Usuario admin = crearUsuario(1L, "admin", "hash-admin", "ADMIN", true);
        when(usuarioDao.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("claveErronea", "hash-admin")).thenReturn(false);

        ForbiddenOperationException ex = assertThrows(ForbiddenOperationException.class,
                () -> usuarioService.limpiarBitacoraAdmin("admin", "claveErronea", "admin"));

        assertEquals("La contrasena de confirmacion no es valida.", ex.getMessage());
        verify(bitacoraAdminDao, never()).deleteAll();
    }

    @Test
    void deberiaEliminarUsuarioNoAdminYRegistrarBitacora() {
        Usuario usuario = crearUsuario(8L, "joyux", "hash", "USER", true);
        when(usuarioDao.findById(8L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(8L, "admin");

        verify(usuarioDao).delete(usuario);
        verify(bitacoraAdminDao).save(any(BitacoraAdmin.class));
    }

    private Usuario crearUsuario(Long id, String username, String password, String rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuario;
    }
}
