package com.otakucenter.service;

import com.otakucenter.model.BitacoraAdmin;
import com.otakucenter.model.Usuario;
import java.util.List;

public interface UsuarioService {

    Usuario buscarPorUsername(String username);

    long contarUsuarios();

    List<Usuario> listarTodos();

    List<BitacoraAdmin> listarBitacoraAdminReciente();

    void limpiarBitacoraAdmin(String usernameConfirmacion, String passwordConfirmacion, String usernameActor);

    void registrarAccesoExitoso(String username);

    void sincronizarUsuarioProyecto(String username, String password, String rol, boolean activo);

    void crearUsuario(String username, String password, String rol, boolean activo, String usernameActor);

    void actualizarEstado(Long id, boolean activo, String usernameActor);

    void actualizarRol(Long id, String nuevoRol, String usernameActor);

    void actualizarPassword(Long id, String nuevaPassword, String usernameActor);

    void eliminarUsuario(Long id, String usernameActor);
}
