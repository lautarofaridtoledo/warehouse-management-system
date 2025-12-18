package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.RolRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Legacy wrapper for Rol service to keep by-type injections working.
 * Delegates to the module implementation in modules/security/roles.
 */
@Deprecated
@Service("legacyRolService")
public class RolServiceImpl implements GenericService<Rol, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.application.RolServiceImpl delegate;

    @Autowired
    public RolServiceImpl(com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.application.RolServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<Rol>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<Rol> buscarPorId(Long id) {
        return delegate.buscarPorId(id);
    }

    @Override
    public Rol crear(Rol rol) {
        return delegate.crear(rol);
    }

    @Override
    public Rol actualizar(Rol rol) {
        return delegate.actualizar(rol);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }
}
