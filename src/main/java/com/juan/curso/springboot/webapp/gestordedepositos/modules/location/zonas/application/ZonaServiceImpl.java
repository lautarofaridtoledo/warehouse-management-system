package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ZonaRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service para Zonas.
 *
 * Implementación real (copiada del legacy) para evitar ciclos.
 */
@Service("zonaService")
public class ZonaServiceImpl implements GenericService<Zona, Long> {

    private final ZonaRepositorio zonaRepositorio;

    public ZonaServiceImpl(ZonaRepositorio zonaRepositorio) {
        this.zonaRepositorio = zonaRepositorio;
    }

    @Override
    public Optional<List<Zona>> buscarTodos() {
        try {
            return Optional.of(zonaRepositorio.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Zona> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return zonaRepositorio.findById(id);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Zona no encontrada con ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Zona crear(Zona zona) {
        try {
            return zonaRepositorio.save(zona);
        } catch (Exception e) {
            e.printStackTrace();
            return zona;
        }
    }

    @Override
    public Zona actualizar(Zona zona) throws RecursoNoEncontradoException {
        try {
            return zonaRepositorio.save(zona);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Zona no encontrado con ID: " + zona.getIdZona());
        } catch (Exception e) {
            e.printStackTrace();
            return zona;
        }
    }

    @Override
    public void eliminar(Long id) {
        if (!zonaRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Zona no encontrada con ID: " + id);
        }
        zonaRepositorio.deleteById(id);
    }
}
