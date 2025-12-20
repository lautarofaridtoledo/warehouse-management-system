package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.persistence.DetalleDespachoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Lógica de DetalleDespacho dentro del módulo Orders/Despacho.
 *
 * Importante: no delega al servicio legacy para evitar ciclos de beans.
 */
@Service("detalleDespachoService")
public class DetalleDespachoServiceImpl implements GenericService<DetalleDespacho, Long> {

    private final DetalleDespachoRepositorio detalleDespachoRepositorio;

    public DetalleDespachoServiceImpl(DetalleDespachoRepositorio detalleDespachoRepositorio) {
        this.detalleDespachoRepositorio = detalleDespachoRepositorio;
    }

    @Override
    public Optional<List<DetalleDespacho>> buscarTodos() {
        try {
            return Optional.of(detalleDespachoRepositorio.findAll());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<DetalleDespacho> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return detalleDespachoRepositorio.findById(id);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Detalle de despacho con id " + id + " no encontrado");
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public DetalleDespacho crear(DetalleDespacho detalleDespacho) {
        try {
            detalleDespacho = detalleDespachoRepositorio.save(detalleDespacho);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return detalleDespacho;
    }

    @Override
    public DetalleDespacho actualizar(DetalleDespacho detalleDespacho) throws RecursoNoEncontradoException {
        try {
            detalleDespacho = detalleDespachoRepositorio.save(detalleDespacho);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException(
                    "Detalle de despacho con id " + detalleDespacho.getIdDetalleDespacho() + " no encontrado");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detalleDespacho;
    }

    @Override
    public void eliminar(Long id) {
        try {
            detalleDespachoRepositorio.deleteById(id);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Detalle de despacho con id " + id + " no encontrado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean ExistePorId(Long id) {
        return detalleDespachoRepositorio.existsById(id);
    }
}
