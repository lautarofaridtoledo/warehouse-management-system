package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.OrdenRecepcionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenes-recepcion")
public class OrdenRecepcionController {

    private final OrdenRecepcionServiceImpl ordenRecepcionService;

    public OrdenRecepcionController(OrdenRecepcionServiceImpl ordenRecepcionService) {
        this.ordenRecepcionService = ordenRecepcionService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todas las ordenes de recepcion")
    public ResponseEntity<List<OrdenRecepcionDTO>> buscarTodos() {
        List<OrdenRecepcionDTO> ordenesDTO = ordenRecepcionService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron órdenes de recepción"))
                .stream()
                .map(OrdenRecepcionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordenesDTO);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca una orden de recepcion por su id")
    public ResponseEntity<OrdenRecepcionDTO> buscar(@RequestParam Long id) {
        OrdenRecepcion orden = ordenRecepcionService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

        return ResponseEntity.ok(new OrdenRecepcionDTO(orden));
    }

    @PostMapping("/crear")
    @Operation(summary = "Crea una orden de recepción y actualiza inventario automáticamente")
    public ResponseEntity<OrdenRecepcionDTO> crearOrdenRecepcion(@RequestBody OrdenRecepcionDTO dto) {
        OrdenRecepcion creada = ordenRecepcionService.procesarEntradaMercaderia(dto);
        return new ResponseEntity<>(new OrdenRecepcionDTO(creada), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Actualiza una orden de recepción completa (cabecera y detalles), ajustando el stock automáticamente.")
    public ResponseEntity<OrdenRecepcionDTO> actualizarOrdenCompleta(@RequestParam Long id, @RequestBody OrdenRecepcionDTO dto) {
        OrdenRecepcion actualizada = ordenRecepcionService.procesarModificacionOrden(id, dto);
        return ResponseEntity.ok(new OrdenRecepcionDTO(actualizada));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Elimina una orden de recepción y revierte el stock ingresado")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        ordenRecepcionService.eliminarConReversion(id);
        return ResponseEntity.ok("Orden eliminada y stock revertido correctamente.");
    }

    @PutMapping("/actualizar-estado")
    @Operation(summary = "Actualiza el estado de la orden")
    public ResponseEntity<String> actualizarEstado(@RequestParam Long id, @RequestParam EstadosDeOrden estado) {
        ordenRecepcionService.updateOrderState(id, estado);
        return ResponseEntity.ok("Estado actualizado correctamente.");
    }
}
