package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.api.dto.InventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.UbicacionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * Application Service que orquesta los casos de uso de Inventario.
 *
 * Objetivo: mover reglas/validaciones/ajustes de ocupación fuera del controller,
 * manteniendo rutas y comportamiento legacy.
 */
@Service
public class InventarioApplicationService {

    private final ProductoServiceImpl productoService;
    private final UbicacionServiceImpl ubicacionService;
    private final InventarioServiceImpl inventarioService;

    public InventarioApplicationService(ProductoServiceImpl productoService,
                                        UbicacionServiceImpl ubicacionService,
                                        InventarioServiceImpl inventarioService) {
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
        this.inventarioService = inventarioService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Inventario crear(InventarioDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El inventario no puede ser nulo");
        }
        if (dto.getUbicacionId() == null) {
            throw new IllegalArgumentException("Debe indicar ubicacionId");
        }
        if (dto.getProductoId() == null) {
            throw new IllegalArgumentException("Debe indicar productoId");
        }

        var ubicacion = ubicacionService.buscarPorId(dto.getUbicacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

        int espacioDisponible = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();
        if (dto.getCantidad() > espacioDisponible) {
            throw new CapacidadExcedida("La ubicación no tiene capacidad suficiente. Disponible: " + espacioDisponible);
        }

        productoService.buscarPorId(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Inventario inventario = new Inventario();
        inventario.setUbicacionId(dto.getUbicacionId());
        inventario.setProductoId(dto.getProductoId());
        inventario.setCantidad(dto.getCantidad());
        inventario.setFecha_actualizacion(Calendar.getInstance().getTime());

        inventario = inventarioService.crear(inventario);

        ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + inventario.getCantidad());
        ubicacionService.actualizar(ubicacion);

        return inventario;
    }

    @Transactional(rollbackFor = Exception.class)
    public Inventario actualizar(Long id, InventarioDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("Debe indicar id");
        }
        if (dto == null) {
            throw new IllegalArgumentException("El inventario no puede ser nulo");
        }
        if (dto.getUbicacionId() == null) {
            throw new IllegalArgumentException("Debe indicar ubicacionId");
        }
        if (dto.getProductoId() == null) {
            throw new IllegalArgumentException("Debe indicar productoId");
        }

        Inventario inventarioExistente = inventarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        var ubicacionAnterior = ubicacionService.buscarPorId(inventarioExistente.getUbicacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación anterior no encontrada"));

        int cantidadAnterior = inventarioExistente.getCantidad();

        var ubicacionNueva = ubicacionService.buscarPorId(dto.getUbicacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación nueva no encontrada"));

        productoService.buscarPorId(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        int cantidadNueva = dto.getCantidad();

        // Si cambia de ubicación
        if (!ubicacionAnterior.getIdUbicacion().equals(ubicacionNueva.getIdUbicacion())) {
            // Restar de ubicación anterior
            int ocupadoAnterior = Math.max(0, ubicacionAnterior.getOcupadoActual() - cantidadAnterior);
            ubicacionAnterior.setOcupadoActual(ocupadoAnterior);
            ubicacionService.actualizar(ubicacionAnterior);

            // Validar capacidad en la nueva
            int espacioDisponible = ubicacionNueva.getCapacidadMaxima() - ubicacionNueva.getOcupadoActual();
            if (cantidadNueva > espacioDisponible) {
                throw new CapacidadExcedida("La nueva ubicación no tiene capacidad suficiente");
            }

            // Sumar en la nueva
            ubicacionNueva.setOcupadoActual(ubicacionNueva.getOcupadoActual() + cantidadNueva);
            ubicacionService.actualizar(ubicacionNueva);
        } else {
            // Misma ubicación
            int diferencia = cantidadNueva - cantidadAnterior;

            if (diferencia > 0) {
                int espacioDisponible = ubicacionNueva.getCapacidadMaxima() - ubicacionNueva.getOcupadoActual();
                if (diferencia > espacioDisponible) {
                    throw new CapacidadExcedida("La ubicación no tiene capacidad suficiente para aumentar esta cantidad");
                }
            }

            ubicacionNueva.setOcupadoActual(Math.max(0, ubicacionNueva.getOcupadoActual() + diferencia));
            ubicacionService.actualizar(ubicacionNueva);
        }

        inventarioExistente.setCantidad(cantidadNueva);
        inventarioExistente.setUbicacionId(dto.getUbicacionId());
        inventarioExistente.setProductoId(dto.getProductoId());
        inventarioExistente.setFecha_actualizacion(Calendar.getInstance().getTime());

        return inventarioService.actualizar(inventarioExistente);
    }

    @Transactional(rollbackFor = Exception.class)
    public void eliminar(Long id) {
        Inventario inventario = inventarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        var ubicacion = ubicacionService.buscarPorId(inventario.getUbicacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

        int nuevoOcupado = Math.max(0, ubicacion.getOcupadoActual() - inventario.getCantidad());
        ubicacion.setOcupadoActual(nuevoOcupado);
        ubicacionService.actualizar(ubicacion);

        inventarioService.eliminar(id);
    }
}
