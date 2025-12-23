package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api.dto.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.application.MovimientoInventarioApplicationService;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.application.MovimientoInventarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/movimientoInventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioServiceImpl movimientoService;
    private final MovimientoInventarioApplicationService applicationService;

    public MovimientoInventarioController(MovimientoInventarioServiceImpl movimientoService,
                                         MovimientoInventarioApplicationService applicationService) {
        this.movimientoService = movimientoService;
        this.applicationService = applicationService;
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
        MovimientoInventario creado = applicationService.crearMovimiento(dto);
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
        MovimientoInventario actualizado = applicationService.editarMovimiento(id, dto);
        return ResponseEntity.ok(new MovimientoInventarioDTO(actualizado));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Elimina un movimiento y revierte los cambios de stock")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        applicationService.eliminarMovimiento(id);
        return ResponseEntity.ok("Movimiento eliminado y stock revertido correctamente.");
    }
}
