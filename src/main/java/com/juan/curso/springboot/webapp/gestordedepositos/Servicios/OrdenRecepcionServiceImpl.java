package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el servicio real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.OrdenRecepcionServiceImpl}.
 *
 * Mantener este wrapper permite migrar imports por etapas sin romper wiring.
 */
@Deprecated(forRemoval = true)
@Service("legacyOrdenRecepcionService")
public class OrdenRecepcionServiceImpl implements GenericService<OrdenRecepcion, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.OrdenRecepcionServiceImpl delegate;

    public OrdenRecepcionServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.OrdenRecepcionServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<OrdenRecepcion>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<OrdenRecepcion> buscarPorId(Long id) {
        return delegate.buscarPorId(id);
    }

    @Override
    public OrdenRecepcion crear(OrdenRecepcion ordenRecepcion) {
        return delegate.crear(ordenRecepcion);
    }

    @Override
    public OrdenRecepcion actualizar(OrdenRecepcion ordenRecepcion) {
        return delegate.actualizar(ordenRecepcion);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }

    public OrdenRecepcion procesarEntradaMercaderia(OrdenRecepcionDTO dto) {
        return delegate.procesarEntradaMercaderia(dto);
    }

    public OrdenRecepcion procesarModificacionOrden(Long idOrden, OrdenRecepcionDTO dto) {
        return delegate.procesarModificacionOrden(idOrden, dto);
    }

    public void eliminarConReversion(Long id) {
        delegate.eliminarConReversion(id);
    }

    public void updateOrderState(Long id, EstadosDeOrden estado) {
        delegate.updateOrderState(id, estado);
    }
}
