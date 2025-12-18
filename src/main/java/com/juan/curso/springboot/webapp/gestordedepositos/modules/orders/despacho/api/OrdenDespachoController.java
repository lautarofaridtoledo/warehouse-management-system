package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.api;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.OrdenDespachoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenes-despacho")
public class OrdenDespachoController {

    private final OrdenDespachoServiceImpl ordenDespachoService;

    public OrdenDespachoController(OrdenDespachoServiceImpl ordenDespachoService) {
        this.ordenDespachoService = ordenDespachoService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        List<OrdenDespacho> ordenes = ordenDespachoService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron órdenes"));

        List<OrdenDespachoDTO> dtoList = ordenes.stream()
                .map(OrdenDespachoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        return ordenDespachoService.buscarPorId(id)
                .map(o -> new ResponseEntity<>(new OrdenDespachoDTO(o), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/crear")
    @Operation(summary = "Crea una orden de despacho y descuenta stock")
    public ResponseEntity<?> crear(@RequestBody OrdenDespachoDTO dto) {
        try {
            OrdenDespacho creada = ordenDespachoService.procesarSalidaMercaderia(dto);
            return new ResponseEntity<>(new OrdenDespachoDTO(creada), HttpStatus.CREATED);
        } catch (StockInsuficienteException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Actualiza una orden de despacho completa, revirtiendo stock anterior y aplicando el nuevo")
    public ResponseEntity<?> actualizarOrdenCompleta(@RequestParam Long id, @RequestBody OrdenDespachoDTO dto) {
        try {
            OrdenDespacho actualizada = ordenDespachoService.procesarModificacionOrden(id, dto);
            return new ResponseEntity<>(new OrdenDespachoDTO(actualizada), HttpStatus.OK);
        } catch (StockInsuficienteException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Elimina una orden y devuelve el stock")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            ordenDespachoService.eliminarConReversion(id);
            return ResponseEntity.ok("Orden eliminada y stock repuesto con éxito");
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
