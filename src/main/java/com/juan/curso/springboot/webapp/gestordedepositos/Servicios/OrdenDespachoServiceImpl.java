package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el servicio real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.OrdenDespachoServiceImpl}.
 *
 * Mantener este wrapper permite migrar imports por etapas sin romper wiring.
 */
@Deprecated(forRemoval = true)
@Service("legacyOrdenDespachoService")
public class OrdenDespachoServiceImpl implements GenericService<OrdenDespacho, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.OrdenDespachoServiceImpl delegate;

    public OrdenDespachoServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.OrdenDespachoServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<OrdenDespacho>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<OrdenDespacho> buscarPorId(Long id) {
        return delegate.buscarPorId(id);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }

    @Override
    public OrdenDespacho crear(OrdenDespacho entity) {
        return delegate.crear(entity);
    }

    @Override
    public OrdenDespacho actualizar(OrdenDespacho entity) {
        return delegate.actualizar(entity);
    }

    public boolean ExistePorId(Long id) {
        return delegate.ExistePorId(id);
    }

    public OrdenDespacho procesarSalidaMercaderia(OrdenDespachoDTO dto) {
        return delegate.procesarSalidaMercaderia(dto);
    }

    public OrdenDespacho procesarModificacionOrden(Long idOrden, OrdenDespachoDTO dto) {
        return delegate.procesarModificacionOrden(idOrden, dto);
    }

    public void eliminarConReversion(Long id) {
        delegate.eliminarConReversion(id);
    }
}