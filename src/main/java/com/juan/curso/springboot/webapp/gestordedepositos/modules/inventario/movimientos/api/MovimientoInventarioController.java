package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api.dto.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.application.MovimientoInventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.UbicacionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/movimientoInventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioServiceImpl movimientoService;
    private final ProductoServiceImpl productoService;
    private final UbicacionServiceImpl ubicacionService;

    public MovimientoInventarioController(MovimientoInventarioServiceImpl movimientoService,
                                         ProductoServiceImpl productoService,
                                         UbicacionServiceImpl ubicacionService) {
        this.movimientoService = movimientoService;
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Lista todos los movimientos de inventario")
    public ResponseEntity<List<MovimientoInventarioDTO>> buscarTodos() {
        List<MovimientoInventarioDTO> movimientos = movimientoService.buscarTodos()
                .orElse(List.of())
                .stream()
                .map(MovimientoInventarioDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(movimientos);
    }

    @PostMapping("/crearMovimiento")
    @Operation(summary = "Crea un movimiento y actualiza el stock en origen y destino")
    public ResponseEntity<MovimientoInventarioDTO> crear(@RequestBody MovimientoInventarioDTO dto) {
        validarPayloadMovimiento(dto);

        if (dto.getFecha() != null && dto.getFecha().after(new Date())) {
            throw new IllegalArgumentException("La fecha del movimiento no puede ser futura");
        }

        Producto producto = productoService.buscarPorId(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Ubicacion origen = ubicacionService.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación origen no encontrada"));

        Ubicacion destino = ubicacionService.buscarPorId(dto.getUbicacionDestino().getIdUbicacion())
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
        MovimientoInventario movInventario = new MovimientoInventario(
                dto.getIdMovimientoInventario(), producto, origen, destino, cantidad, fechaMovimiento, dto.getEstado());

        MovimientoInventario creado = movimientoService.crear(movInventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MovimientoInventarioDTO(creado));
    }

    @GetMapping("/buscar")
    public ResponseEntity<MovimientoInventarioDTO> buscar(@RequestParam Long id) {
        MovimientoInventario movimiento = movimientoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento de inventario no encontrado con ID: " + id));

        return ResponseEntity.ok(new MovimientoInventarioDTO(movimiento));
    }

    @PutMapping("/editar")
    @Operation(summary = "Este metodo edita un movimiento")
    public ResponseEntity<MovimientoInventarioDTO> editar(@RequestParam Long id, @RequestBody MovimientoInventarioDTO dto) {
        validarPayloadMovimiento(dto);

        if (dto.getFecha() != null && dto.getFecha().after(new Date())) {
            throw new IllegalArgumentException("La fecha del movimiento no puede ser futura");
        }

        MovimientoInventario movimiento = movimientoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento de inventario no encontrado con ID: " + id));

        Ubicacion origenNuevo = ubicacionService.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación origen no encontrada"));

        Ubicacion destinoNuevo = ubicacionService.buscarPorId(dto.getUbicacionDestino().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación destino no encontrada"));

        Producto producto = productoService.buscarPorId(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Ubicacion origenAnterior = ubicacionService.buscarPorId(movimiento.getUbicacionOrigen().getIdUbicacion())
                .orElse(origenNuevo);
        Ubicacion destinoAnterior = ubicacionService.buscarPorId(movimiento.getUbicacionDestino().getIdUbicacion())
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

        // Revertir movimiento anterior
        destinoAnterior.setOcupadoActual(destinoAnterior.getOcupadoActual() - cantidadAnterior);
        if (destinoAnterior.getOcupadoActual() < 0) {
            restaurarOcupaciones(ocupacionesOriginales);
            throw new IllegalStateException("La ubicación destino original quedaría con stock negativo");
        }
        origenAnterior.setOcupadoActual(origenAnterior.getOcupadoActual() + cantidadAnterior);

        // Validar y aplicar nuevo movimiento
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

        movimiento.setProducto(producto);
        movimiento.setUbicacionOrigen(origenNuevo);
        movimiento.setUbicacionDestino(destinoNuevo);
        movimiento.setCantidad(cantidadNueva);
        movimiento.setEstado(dto.getEstado());
        movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : movimiento.getFecha());

        MovimientoInventario actualizado = movimientoService.actualizar(movimiento);
        return ResponseEntity.ok(new MovimientoInventarioDTO(actualizado));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Elimina un movimiento y revierte los cambios de stock")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        movimientoService.revertirYEliminar(id);
        return ResponseEntity.ok("Movimiento eliminado y stock revertido correctamente.");
    }

    private void validarPayloadMovimiento(MovimientoInventarioDTO dto) {
        if (dto == null || dto.getProducto() == null || dto.getUbicacionOrigen() == null
                || dto.getUbicacionDestino() == null || dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("Payload incompleto o cantidad inválida");
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
