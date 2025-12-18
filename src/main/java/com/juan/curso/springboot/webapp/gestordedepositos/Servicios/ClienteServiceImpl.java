package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el service real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.application.ClientServiceImpl}.
 */
@Deprecated(forRemoval = true)
@Service
public class ClienteServiceImpl implements GenericService<Cliente, Long>{

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.application.ClientServiceImpl delegate;

    public ClienteServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.application.ClientServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<Cliente>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return delegate.buscarPorId(id);
    }

    @Override
    public Cliente crear(Cliente cliente) {
        return delegate.crear(cliente);
    }

    @Override
    public Cliente actualizar(Cliente cliente) {
        return delegate.actualizar(cliente);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }
}
