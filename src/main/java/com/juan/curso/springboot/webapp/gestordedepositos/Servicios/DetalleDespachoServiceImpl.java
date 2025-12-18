package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.DetalleDespachoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper legacy para mantener compatibilidad con inyecciones por tipo.
 *
 * La implementación real vive en modules/orders/despacho/application.
 */
@Deprecated
@Service("legacyDetalleDespachoService")
public class DetalleDespachoServiceImpl implements GenericService<DetalleDespacho, Long>{

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.DetalleDespachoServiceImpl delegate;

    @Autowired
    public DetalleDespachoServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.DetalleDespachoServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<DetalleDespacho>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<DetalleDespacho> buscarPorId(Long id) throws RecursoNoEncontradoException {
        return delegate.buscarPorId(id);
    }

    @Override
    public DetalleDespacho crear(DetalleDespacho detalleDespacho) {
        return delegate.crear(detalleDespacho);
    }

    @Override
    public DetalleDespacho actualizar(DetalleDespacho detalleDespacho) throws RecursoNoEncontradoException{
        return delegate.actualizar(detalleDespacho);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }

    public boolean ExistePorId(Long id) {
        return delegate.ExistePorId(id);
    }
}
