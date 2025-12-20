package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.persistence.UsuarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service para Usuarios.
 *
 * Implementación real (copiada del legacy) para evitar ciclos.
 */
@Service("usuarioService")
public class UsuarioServiceImpl implements GenericService<Usuario, Long> {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServiceImpl(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Usuario>> buscarTodos() {
        return Optional.of(usuarioRepositorio.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    @Override
    @Transactional
    public Usuario crear(Usuario usuario) {
        if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email '" + usuario.getEmail() + "' ya está registrado.");
        }
        return usuarioRepositorio.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizar(Usuario usuarioEditado) {
        Usuario usuarioActual = usuarioRepositorio.findById(usuarioEditado.getIdUsuario())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró el usuario con ID: " + usuarioEditado.getIdUsuario()));

        if (!usuarioActual.getEmail().equalsIgnoreCase(usuarioEditado.getEmail())) {
            if (usuarioRepositorio.existsByEmail(usuarioEditado.getEmail())) {
                throw new RuntimeException("El email '" + usuarioEditado.getEmail() + "' ya está registrado.");
            }
        }
        return usuarioRepositorio.save(usuarioEditado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Usuario no encontrado.");
        }
        usuarioRepositorio.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Usuario getByNombreEquals(String nombre) {
        return usuarioRepositorio.getByNombreEquals(nombre);
    }

    @Transactional(readOnly = true)
    public Usuario getByEmailEquals(String email) {
        return usuarioRepositorio.getByEmailEquals(email);
    }

    @Transactional(readOnly = true)
    public Optional<List<Usuario>> buscarPorRolId(Long rolId) {
        return Optional.ofNullable(usuarioRepositorio.getByRolId(rolId));
    }
}
