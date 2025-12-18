package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.DetalleRecepcionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Wrapper legacy para mantener compatibilidad con inyecciones por tipo.
 *
 * La implementación real vive en modules/orders/recepcion/application.
 */
@Deprecated
@Service("legacyDetalleRecepcionService")
public class DetalleRecepcionServiceImpl implements GenericService<DetalleRecepcion, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.DetalleRecepcionServiceImpl delegate;

    @Autowired
    public DetalleRecepcionServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.DetalleRecepcionServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<DetalleRecepcion>> buscarTodos() {
        return delegate.buscarTodos();
    }
    @Override
    public Optional<DetalleRecepcion> buscarPorId(Long id) {
        return delegate.buscarPorId(id);

    }

    @Override
    public DetalleRecepcion crear(DetalleRecepcion detalle) {
        return delegate.crear(detalle);
    }

    public List<DetalleRecepcion> crearTodos(List<DetalleRecepcion> detalles) {
        return delegate.crearTodos(detalles);
    }

    @Override
    public DetalleRecepcion actualizar(DetalleRecepcion detalle) {
        return delegate.actualizar(detalle);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }

    public void eliminarTodos(List<DetalleRecepcion> detalles){
        delegate.eliminarTodos(detalles);
    }

    public Optional<List<DetalleRecepcion>> buscarDetallesPorOrden(Long orden) {
        return delegate.buscarDetallesPorOrden(orden);
    }

}
