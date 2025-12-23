package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.api.dto.InventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioApplicationService;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller activo para Inventario (módulo inventario).
 *
 * Mantiene las rutas legacy para compatibilidad:
 * - /GestorDeDepositos/inventario/**
 */
@RestController
@RequestMapping("GestorDeDepositos/inventario")
public class InventarioController {

    private final InventarioServiceImpl inventarioService;
    private final InventarioApplicationService inventarioApplicationService;

    public InventarioController(InventarioServiceImpl inventarioService,
                                InventarioApplicationService inventarioApplicationService) {
        this.inventarioService = inventarioService;
        this.inventarioApplicationService = inventarioApplicationService;
    }

    @GetMapping("/stockTotalPorIdProducto")
    @Operation(summary = "Calcula el stock total de un producto por su ID")
    public ResponseEntity<Integer> getStockTotalPorId(@RequestParam Long id) {
        int stockTotal = inventarioService.calcularStockTotalPorIdProducto(id);
        return ResponseEntity.ok(stockTotal);
    }

    @GetMapping("/stockTotalPorCodigoSku")
    @Operation(summary = "Calcula el stock total de un producto por su Codigo SKU")
    public ResponseEntity<Integer> getStockTotalPorCodigoSku(@RequestParam String codigoSku) {
        int stockTotal = inventarioService.calcularStockTotalPorCodigoSku(codigoSku);
        return ResponseEntity.ok(stockTotal);
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los inventarios")
    public ResponseEntity<List<InventarioDTO>> buscarTodos() {
        List<InventarioDTO> dtoList = inventarioService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los inventarios"))
                .stream()
                .map(InventarioDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Busca un inventario por su ID")
    public ResponseEntity<InventarioDTO> buscarPorId(@RequestParam Long id) {
        Inventario inventario = inventarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        return ResponseEntity.ok(new InventarioDTO(inventario));
    }

    @GetMapping("/buscarPorCodigoSkuProducto")
    @Operation(summary = "Este metodo busca inventarios por codigo sku producto")
    public ResponseEntity<List<InventarioDTO>> buscarPorCodigoSku(@RequestParam String codigoSku) {
        List<Inventario> inventarios = inventarioService.buscarPorCodigoSku(codigoSku);

        if (inventarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron inventarios con ese código SKU");
        }

        List<InventarioDTO> dtoList = inventarios.stream()
                .map(InventarioDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un inventario")
    public ResponseEntity<InventarioDTO> crear(@RequestBody InventarioDTO inventarioDTO) {
        Inventario inventario = inventarioApplicationService.crear(inventarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new InventarioDTO(inventario));
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo busca un inventario por id y lo actualiza")
    public ResponseEntity<InventarioDTO> actualizar(@RequestParam Long id,
                                                    @RequestBody InventarioDTO inventarioDTO) {
        Inventario actualizado = inventarioApplicationService.actualizar(id, inventarioDTO);
        return ResponseEntity.ok(new InventarioDTO(actualizado));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un inventario por su id")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        inventarioApplicationService.eliminar(id);
        return ResponseEntity.ok("Inventario eliminado con éxito");
    }
}
