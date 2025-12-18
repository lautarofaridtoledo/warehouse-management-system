package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper legacy para mantener compatibilidad con inyecciones por tipo.
 *
 * La implementación real vive en modules/security/users/application.
 */
@Deprecated
@Service("legacyUsuarioService")
public class UsuarioServiceImpl implements GenericService<Usuario, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application.UsuarioServiceImpl delegate;

    public UsuarioServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application.UsuarioServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Usuario>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return delegate.buscarPorId(id);
    }

    @Override
    @Transactional
    public Usuario crear(Usuario usuario) {
        return delegate.crear(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizar(Usuario usuarioEditado) {
        return delegate.actualizar(usuarioEditado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }

    @Transactional(readOnly = true)
    public Usuario getByNombreEquals(String nombre){
        return delegate.getByNombreEquals(nombre);
    }

    @Transactional(readOnly = true)
    public Usuario getByEmailEquals(String email){
        return delegate.getByEmailEquals(email);
    }

    @Transactional(readOnly = true)
    public Optional<List<Usuario>> buscarPorRol(Rol rol) {
        return delegate.buscarPorRol(rol);
    }
}