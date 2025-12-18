package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ReporteUbicacionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.CategoriasProducto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Wrapper legacy para mantener compatibilidad con inyecciones por tipo.
 *
 * La implementación real vive en modules/location/ubicaciones/application.
 */
@Deprecated
@Service("legacyUbicacionService")
public class UbicacionServiceImpl implements GenericService<Ubicacion, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.UbicacionServiceImpl delegate;

    @Autowired
    public UbicacionServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.UbicacionServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<Ubicacion>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<Ubicacion> buscarPorId(Long id) throws RecursoNoEncontradoException {
        return delegate.buscarPorId(id);
    }

    @Override
    public Ubicacion crear(Ubicacion ubicacion) {
        return delegate.crear(ubicacion);
    }

    @Override
    public Ubicacion actualizar(Ubicacion ubicacion) throws RecursoNoEncontradoException {
        return delegate.actualizar(ubicacion);
    }

    @Override
    public void eliminar(Long id) throws RecursoNoEncontradoException {
        delegate.eliminar(id);
    }

    public Ubicacion buscarUbicacionSegunCantidad(int cantidad) {
        return delegate.buscarUbicacionSegunCantidad(cantidad);
    }

    public Ubicacion buscarMejorUbicacion(CategoriasProducto categoria, int cantidad) {
        return delegate.buscarMejorUbicacion(categoria, cantidad);
    }

    public List<ReporteUbicacionDTO> obtenerEspacioDeUbicaciones() {
    return delegate.obtenerEspacioDeUbicaciones();
    }

    public Ubicacion obtenerUbicacionConMayorEspacioDisponible() {
        return delegate.obtenerUbicacionConMayorEspacioDisponible();
    }

    public int obtenerCapacidadMaximaDisponibleDeUbicaciones() {
        return delegate.obtenerCapacidadMaximaDisponibleDeUbicaciones();
    }
}
