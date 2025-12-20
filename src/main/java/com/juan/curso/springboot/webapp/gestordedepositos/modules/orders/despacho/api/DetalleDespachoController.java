package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.api.dto.DetalleDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.DetalleDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application.OrdenDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/detalles-despacho")
public class DetalleDespachoController {

    private final DetalleDespachoServiceImpl detalleDespachoService;
    private final ProductoServiceImpl productoService;
    private final OrdenDespachoServiceImpl ordenDespachoService;
    private final InventarioServiceImpl inventarioService;

    public DetalleDespachoController(DetalleDespachoServiceImpl detalleDespachoService,
                                    ProductoServiceImpl productoService,
                                    OrdenDespachoServiceImpl ordenDespachoService,
                                    InventarioServiceImpl inventarioService) {
        this.detalleDespachoService = detalleDespachoService;
        this.productoService = productoService;
        this.ordenDespachoService = ordenDespachoService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca todos los detalles de despachos guardados en la base de datos")
    public ResponseEntity<List<DetalleDespachoDTO>> buscarTodos() {
        List<DetalleDespacho> detalles = detalleDespachoService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron detalles de despacho"));

        List<DetalleDespachoDTO> dtoList = detalles.stream()
                .map(DetalleDespachoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca un detalle de despacho por el id tipo LONG")
    public ResponseEntity<DetalleDespachoDTO> buscar(@RequestParam Long id) {
        DetalleDespacho detalle = detalleDespachoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrado con ID: " + id));

        return ResponseEntity.ok(new DetalleDespachoDTO(detalle));
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un detalle de despacho. Valida que el producto y el inventario existan como tambien la orden a la que se quiere agregar el detalle")
    public ResponseEntity<DetalleDespachoDTO> crear(@RequestBody DetalleDespachoDTO dto) {
    if (dto.getProductoId() == null) {
        throw new IllegalArgumentException("Debe indicar productoId");
    }

    productoService.buscarPorId(dto.getProductoId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

    Inventario inventario = inventarioService.buscarPorId(dto.getProductoId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        if (dto.getCantidad() > inventario.getCantidad()) {
            throw new StockInsuficienteException("Cantidad insuficiente en inventario");
        }

        OrdenDespacho orden = ordenDespachoService.buscarPorId(dto.getOrdenDespacho().getIdOrdenDespacho())
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

    DetalleDespacho detalle = new DetalleDespacho();
    detalle.setProductoId(dto.getProductoId());
        detalle.setOrdenDespacho(orden);
        detalle.setCantidad(dto.getCantidad());

        inventarioService.disminuirCantidad(detalle);
        detalleDespachoService.crear(detalle);

        return ResponseEntity.status(HttpStatus.CREATED).body(new DetalleDespachoDTO(detalle));
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un detalle de despacho por el id tipo LONG")
    public ResponseEntity<DetalleDespachoDTO> actualizar(@RequestParam Long id, @RequestBody DetalleDespachoDTO dto) {
        DetalleDespacho detalle = detalleDespachoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrado con ID: " + id));

        if (dto.getProductoId() == null) {
            throw new IllegalArgumentException("Debe indicar productoId");
        }

        List<Inventario> inventarios = inventarioService.buscarInventariosPorIdProducto(dto.getProductoId());

        if (inventarios.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No existe inventario para el productoId " + dto.getProductoId());
        }

        inventarios.stream()
                .filter(invent -> invent.getCantidad() >= dto.getCantidad())
                .findFirst()
                .orElseThrow(() -> new StockInsuficienteException("Cantidad insuficiente en inventario"));

    detalle.setProductoId(dto.getProductoId());
        detalle.setCantidad(dto.getCantidad());

        detalleDespachoService.crear(detalle);
        return ResponseEntity.ok(new DetalleDespachoDTO(detalle));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un detalle de una orden de despacho")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        if (!detalleDespachoService.ExistePorId(id)) {
            throw new RecursoNoEncontradoException("Detalle de despacho con id " + id + " no encontrado");
        }
        detalleDespachoService.eliminar(id);
        return ResponseEntity.ok("Detalle eliminado con éxito");
    }
}
