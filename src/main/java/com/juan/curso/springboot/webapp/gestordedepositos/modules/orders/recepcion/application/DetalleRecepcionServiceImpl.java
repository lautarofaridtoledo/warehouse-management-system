package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.DetalleRecepcionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Lógica de DetalleRecepcion dentro del módulo Orders/Recepción.
 *
 * Importante: no delega al servicio legacy para evitar ciclos de beans.
 */
@Service("detalleRecepcionService")
public class DetalleRecepcionServiceImpl implements GenericService<DetalleRecepcion, Long> {

    private final DetalleRecepcionRepositorio detalleRecepcionRepositorio;

    public DetalleRecepcionServiceImpl(DetalleRecepcionRepositorio detalleRecepcionRepositorio) {
        this.detalleRecepcionRepositorio = detalleRecepcionRepositorio;
    }

    @Override
    public Optional<List<DetalleRecepcion>> buscarTodos() {
        try {
            return Optional.of(new ArrayList<>(detalleRecepcionRepositorio.findAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<DetalleRecepcion> buscarPorId(Long id) {
        if (id != null) {
            try {
                Optional<DetalleRecepcion> detalle = detalleRecepcionRepositorio.findById(id);
                if (detalle.isPresent()) {
                    return detalle;
                }
                return Optional.empty();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("El id del detalle recepcion no existe.");
        }
        return Optional.empty();
    }

    @Override
    public DetalleRecepcion crear(DetalleRecepcion detalle) {
        try {
            detalle = detalleRecepcionRepositorio.save(detalle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detalle;
    }

    public List<DetalleRecepcion> crearTodos(List<DetalleRecepcion> detalles) {
        try {
            return detalleRecepcionRepositorio.saveAll(detalles);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudieron guardar los detalles de recepción", e);
        }
    }

    @Override
    public DetalleRecepcion actualizar(DetalleRecepcion detalle) {
        try {
            detalle = detalleRecepcionRepositorio.save(detalle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detalle;
    }

    @Override
    public void eliminar(Long id) {
        try {
            detalleRecepcionRepositorio.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTodos(List<DetalleRecepcion> detalles) {
        try {
            detalleRecepcionRepositorio.deleteAll(detalles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<List<DetalleRecepcion>> buscarDetallesPorOrden(Long orden) {
        try {
            Optional<List<DetalleRecepcion>> detalles = detalleRecepcionRepositorio.findByOrdenRecepcion_IdOrdenRecepcion(orden);
            if (detalles.isPresent()) {
                return detalles;
            } else {
                throw new RecursoNoEncontradoException("No se encontraron detalles para esa orden");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
