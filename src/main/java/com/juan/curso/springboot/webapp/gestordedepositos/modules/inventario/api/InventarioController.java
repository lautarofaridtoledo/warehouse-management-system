package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.api;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.InventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.UbicacionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
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

    private final ProductoServiceImpl productoService;
    private final UbicacionServiceImpl ubicacionService;
    private final InventarioServiceImpl inventarioService;

    public InventarioController(ProductoServiceImpl productoService,
                                UbicacionServiceImpl ubicacionService,
                                InventarioServiceImpl inventarioService) {
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
        this.inventarioService = inventarioService;
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
        Inventario inventario = new Inventario();

        Ubicacion ubicacion = ubicacionService.buscarPorId(inventarioDTO.getUbicacion().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

        int espacioDisponible = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();
        if (inventarioDTO.getCantidad() > espacioDisponible) {
            throw new CapacidadExcedida("La ubicación no tiene capacidad suficiente. Disponible: " + espacioDisponible);
        }

        inventario.setUbicacion(ubicacion);

        Producto producto = productoService.buscarPorId(inventarioDTO.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        inventario.setProducto(producto);
        inventario.setCantidad(inventarioDTO.getCantidad());
        inventario.setFecha_actualizacion(Calendar.getInstance().getTime());

        inventario = inventarioService.crear(inventario);

        ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + inventario.getCantidad());
        ubicacionService.actualizar(ubicacion);

        return new ResponseEntity<>(new InventarioDTO(inventario), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo busca un inventario por id y lo actualiza")
    public ResponseEntity<InventarioDTO> actualizar(@RequestParam Long id,
                                                    @RequestBody InventarioDTO inventarioDTO) {
        Inventario inventarioExistente = inventarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        Ubicacion ubicacionAnterior = inventarioExistente.getUbicacion();
        int cantidadAnterior = inventarioExistente.getCantidad();

        Ubicacion ubicacionNueva = ubicacionService.buscarPorId(inventarioDTO.getUbicacion().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación nueva no encontrada"));

        Producto producto = productoService.buscarPorId(inventarioDTO.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        int cantidadNueva = inventarioDTO.getCantidad();

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
        inventarioExistente.setUbicacion(ubicacionNueva);
        inventarioExistente.setProducto(producto);
        inventarioExistente.setFecha_actualizacion(Calendar.getInstance().getTime());

        Inventario actualizado = inventarioService.actualizar(inventarioExistente);

        return ResponseEntity.ok(new InventarioDTO(actualizado));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un inventario por su id")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        Inventario inventario = inventarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        Ubicacion ubicacion = inventario.getUbicacion();

        int nuevoOcupado = Math.max(0, ubicacion.getOcupadoActual() - inventario.getCantidad());
        ubicacion.setOcupadoActual(nuevoOcupado);
        ubicacionService.actualizar(ubicacion);

        inventarioService.eliminar(id);

        return ResponseEntity.ok("Inventario eliminado con éxito");
    }
}
