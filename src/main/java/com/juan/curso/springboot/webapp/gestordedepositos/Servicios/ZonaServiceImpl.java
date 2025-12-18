package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ZonaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper legacy para mantener compatibilidad con inyecciones por tipo.
 *
 * La implementación real vive en modules/location/zonas/application.
 */
@Deprecated
@Service("legacyZonaService")
public class ZonaServiceImpl implements GenericService<Zona, Long>{

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.application.ZonaServiceImpl delegate;

    @Autowired
    public ZonaServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.application.ZonaServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<Zona>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<Zona> buscarPorId(Long id) throws RecursoNoEncontradoException{
        return delegate.buscarPorId(id);
    }

    @Override
    public Zona crear(Zona zona) {
        return delegate.crear(zona);
    }

    @Override
    public Zona actualizar(Zona zona) throws RecursoNoEncontradoException {
        return delegate.actualizar(zona);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }
}
