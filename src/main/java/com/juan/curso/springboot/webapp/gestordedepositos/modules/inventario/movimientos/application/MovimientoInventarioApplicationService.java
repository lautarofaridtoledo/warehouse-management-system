package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api.dto.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.UbicacionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Orquesta los casos de uso de Movimientos de Inventario.
 *
 * Nota: mantiene el comportamiento legacy (validaciones + ajuste de ocupación)
 * cuando se crea o edita, y delega al service para persistencia/reversión.
 */
@Service
public class MovimientoInventarioApplicationService {

    private final MovimientoInventarioServiceImpl movimientoService;
    private final ProductoServiceImpl productoService;
    private final UbicacionServiceImpl ubicacionService;

    public MovimientoInventarioApplicationService(MovimientoInventarioServiceImpl movimientoService,
                                                 ProductoServiceImpl productoService,
                                                 UbicacionServiceImpl ubicacionService) {
        this.movimientoService = movimientoService;
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
    }

    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventario crearMovimiento(MovimientoInventarioDTO dto) {
        validarPayloadMovimiento(dto);
        validarFechaNoFutura(dto);

        // validar existencia
        productoService.buscarPorId(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Ubicacion origen = ubicacionService.buscarPorId(dto.getUbicacionOrigenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación origen no encontrada"));

        Ubicacion destino = ubicacionService.buscarPorId(dto.getUbicacionDestinoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación destino no encontrada"));

        int cantidad = dto.getCantidad();

        if (origen.getOcupadoActual() < cantidad) {
            throw new StockInsuficienteException("La ubicación de origen no tiene stock suficiente");
        }
        if (destino.getCapacidadMaxima() - destino.getOcupadoActual() < cantidad) {
            throw new CapacidadExcedida("La ubicación de destino no tiene capacidad disponible");
        }

        // mantener comportamiento legacy: actualizar ocupación antes de persistir el movimiento
        origen.setOcupadoActual(origen.getOcupadoActual() - cantidad);
        destino.setOcupadoActual(destino.getOcupadoActual() + cantidad);
        ubicacionService.actualizar(origen);
        ubicacionService.actualizar(destino);

        Date fechaMovimiento = dto.getFecha() != null ? dto.getFecha() : new Date();
        MovimientoInventario movInventario = new MovimientoInventario();
        movInventario.setIdMovimientoInventario(dto.getIdMovimientoInventario());
        movInventario.setProductoId(dto.getProductoId());
        movInventario.setUbicacionOrigenId(dto.getUbicacionOrigenId());
        movInventario.setUbicacionDestinoId(dto.getUbicacionDestinoId());
        movInventario.setCantidad(cantidad);
        movInventario.setFecha(fechaMovimiento);
        movInventario.setEstado(dto.getEstado());

        return movimientoService.crear(movInventario);
    }

    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventario editarMovimiento(Long id, MovimientoInventarioDTO dto) {
        validarPayloadMovimiento(dto);
        validarFechaNoFutura(dto);

        MovimientoInventario movimiento = movimientoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Movimiento de inventario no encontrado con ID: " + id));

        Ubicacion origenNuevo = ubicacionService.buscarPorId(dto.getUbicacionOrigenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación origen no encontrada"));

        Ubicacion destinoNuevo = ubicacionService.buscarPorId(dto.getUbicacionDestinoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación destino no encontrada"));

        // validar producto
        productoService.buscarPorId(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Ubicacion origenAnterior = ubicacionService.buscarPorId(movimiento.getUbicacionOrigenId())
                .orElse(origenNuevo);
        Ubicacion destinoAnterior = ubicacionService.buscarPorId(movimiento.getUbicacionDestinoId())
                .orElse(destinoNuevo);

        if (origenNuevo.getIdUbicacion().equals(origenAnterior.getIdUbicacion())) {
            origenNuevo = origenAnterior;
        }
        if (destinoNuevo.getIdUbicacion().equals(destinoAnterior.getIdUbicacion())) {
            destinoNuevo = destinoAnterior;
        }

        Map<Ubicacion, Integer> ocupacionesOriginales = new HashMap<>();
        Set<Ubicacion> ubicacionesAActualizar = new HashSet<>();
        registrarUbicacion(origenAnterior, ocupacionesOriginales, ubicacionesAActualizar);
        registrarUbicacion(destinoAnterior, ocupacionesOriginales, ubicacionesAActualizar);
        registrarUbicacion(origenNuevo, ocupacionesOriginales, ubicacionesAActualizar);
        registrarUbicacion(destinoNuevo, ocupacionesOriginales, ubicacionesAActualizar);

        int cantidadAnterior = movimiento.getCantidad();
        int cantidadNueva = dto.getCantidad();

        // revertir movimiento anterior
        destinoAnterior.setOcupadoActual(destinoAnterior.getOcupadoActual() - cantidadAnterior);
        if (destinoAnterior.getOcupadoActual() < 0) {
            restaurarOcupaciones(ocupacionesOriginales);
            throw new IllegalStateException("La ubicación destino original quedaría con stock negativo");
        }
        origenAnterior.setOcupadoActual(origenAnterior.getOcupadoActual() + cantidadAnterior);

        // validar y aplicar nuevo movimiento
        if (origenNuevo.getOcupadoActual() < cantidadNueva) {
            restaurarOcupaciones(ocupacionesOriginales);
            throw new StockInsuficienteException("La ubicación de origen no tiene stock suficiente");
        }
        if (destinoNuevo.getCapacidadMaxima() - destinoNuevo.getOcupadoActual() < cantidadNueva) {
            restaurarOcupaciones(ocupacionesOriginales);
            throw new CapacidadExcedida("La ubicación de destino no tiene capacidad disponible");
        }

        origenNuevo.setOcupadoActual(origenNuevo.getOcupadoActual() - cantidadNueva);
        destinoNuevo.setOcupadoActual(destinoNuevo.getOcupadoActual() + cantidadNueva);

        for (Ubicacion ubicacion : ubicacionesAActualizar) {
            ubicacionService.actualizar(ubicacion);
        }

        movimiento.setProductoId(dto.getProductoId());
        movimiento.setUbicacionOrigenId(dto.getUbicacionOrigenId());
        movimiento.setUbicacionDestinoId(dto.getUbicacionDestinoId());
        movimiento.setCantidad(cantidadNueva);
        movimiento.setEstado(dto.getEstado());
        movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : movimiento.getFecha());

        return movimientoService.actualizar(movimiento);
    }

    @Transactional(rollbackFor = Exception.class)
    public void eliminarMovimiento(Long id) {
        movimientoService.revertirYEliminar(id);
    }

    private void validarPayloadMovimiento(MovimientoInventarioDTO dto) {
        if (dto == null || dto.getProductoId() == null || dto.getUbicacionOrigenId() == null
                || dto.getUbicacionDestinoId() == null || dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("Payload incompleto o cantidad inválida");
        }
    }

    private void validarFechaNoFutura(MovimientoInventarioDTO dto) {
        if (dto.getFecha() != null && dto.getFecha().after(new Date())) {
            throw new IllegalArgumentException("La fecha del movimiento no puede ser futura");
        }
    }

    private void registrarUbicacion(Ubicacion ubicacion, Map<Ubicacion, Integer> ocupaciones, Set<Ubicacion> ubicaciones) {
        if (ubicacion != null) {
            ocupaciones.putIfAbsent(ubicacion, ubicacion.getOcupadoActual());
            ubicaciones.add(ubicacion);
        }
    }

    private void restaurarOcupaciones(Map<Ubicacion, Integer> ocupaciones) {
        ocupaciones.forEach(Ubicacion::setOcupadoActual);
    }
}
