package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.api.dto.ReporteUbicacionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.CategoriasProducto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.persistence.UbicacionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application service para Ubicaciones.
 *
 * Implementación real (copiada del legacy) para evitar ciclos.
 */
@Service("ubicacionService")
public class UbicacionServiceImpl implements GenericService<Ubicacion, Long> {

    private final UbicacionRepositorio ubicacionRepositorio;

    public UbicacionServiceImpl(UbicacionRepositorio ubicacionRepositorio) {
        this.ubicacionRepositorio = ubicacionRepositorio;
    }

    @Override
    public Optional<List<Ubicacion>> buscarTodos() {
        try {
            return Optional.of(ubicacionRepositorio.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Ubicacion> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return ubicacionRepositorio.findById(id);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Ubicacion no encontrada con ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Ubicacion crear(Ubicacion ubicacion) {
        try {
            return ubicacionRepositorio.save(ubicacion);
        } catch (Exception e) {
            e.printStackTrace();
            return ubicacion;
        }
    }

    @Override
    public Ubicacion actualizar(Ubicacion ubicacion) throws RecursoNoEncontradoException {
        try {
            return ubicacionRepositorio.save(ubicacion);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Ubicacion no encontrada con ID: " + ubicacion.getIdUbicacion());
        } catch (Exception e) {
            e.printStackTrace();
            return ubicacion;
        }
    }

    @Override
    public void eliminar(Long id) throws RecursoNoEncontradoException {
        try {
            ubicacionRepositorio.deleteById(id);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Ubicacion no encontrada con ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Ubicacion buscarUbicacionSegunCantidad(int cantidad) {
        try {
            List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();
            for (Ubicacion ubicacion : ubicaciones) {
                if (ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual() >= cantidad) {
                    return ubicacion;
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Ubicacion buscarMejorUbicacion(CategoriasProducto categoria, int cantidad) {
        List<Ubicacion> candidatos = ubicacionRepositorio.buscarUbicacionesPorCategoriaYEspacio(categoria, cantidad);

        if (candidatos.isEmpty()) {
            throw new RuntimeException("No hay espacio disponible en ninguna Zona habilitada para " + categoria);
        }

        return candidatos.get(0);
    }

    public List<ReporteUbicacionDTO> obtenerEspacioDeUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();

        return ubicaciones.stream()
                .map(u -> new ReporteUbicacionDTO(
                        u.getIdUbicacion(),
                        u.getCapacidadMaxima(),
                        u.getOcupadoActual()
                ))
                .sorted(Comparator.comparingInt(ReporteUbicacionDTO::getEspacioUtilizado).reversed())
                .collect(Collectors.toList());
    }

    public Ubicacion obtenerUbicacionConMayorEspacioDisponible() {
        List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();

        return ubicaciones.stream()
                .filter(u -> u.getCapacidadMaxima() >= u.getOcupadoActual())
                .max(Comparator.comparingInt(u -> u.getCapacidadMaxima() - u.getOcupadoActual()))
                .orElse(null);
    }

    public int obtenerCapacidadMaximaDisponibleDeUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();

        return ubicaciones.stream()
                .mapToInt(u -> u.getCapacidadMaxima() - u.getOcupadoActual())
                .sum();
    }
}
