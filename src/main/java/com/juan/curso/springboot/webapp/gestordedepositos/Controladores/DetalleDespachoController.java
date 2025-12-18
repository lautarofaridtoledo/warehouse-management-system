package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.InventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// NOTA: Este controller legacy NO está anotado como @RestController para evitar endpoints duplicados.
// La versión activa vive en modules/orders/despacho/api.
@RequestMapping("GestorDeDepositos/detalles-despacho")
public class DetalleDespachoController {

    private final DetalleDespachoServiceImpl detalleDespachoServiceImpl;
    private final ProductoServiceImpl productoServiceImpl;
    private final OrdenDespachoServiceImpl ordenDespachoServiceImpl;
    private final InventarioServiceImpl inventarioServiceImpl;

    @Autowired
    public DetalleDespachoController(DetalleDespachoServiceImpl detalleDespachoServiceImpl,
                                     ProductoServiceImpl productoServiceImpl,
                                     OrdenDespachoServiceImpl ordenDespachoServiceImpl,
                                     InventarioServiceImpl inventarioServiceImpl) {
        this.detalleDespachoServiceImpl = detalleDespachoServiceImpl;
        this.productoServiceImpl = productoServiceImpl;
        this.ordenDespachoServiceImpl = ordenDespachoServiceImpl;
        this.inventarioServiceImpl = inventarioServiceImpl;
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca todos los detalles de despachos guardados en la base de datos")
    public ResponseEntity<List<DetalleDespachoDTO>> buscarTodos() {
        List<DetalleDespacho> detalles = detalleDespachoServiceImpl.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron detalles de despacho"));

        List<DetalleDespachoDTO> dtoList = detalles.stream()
                .map(DetalleDespachoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca un detalle de despacho por el id tipo LONG")
    public ResponseEntity<DetalleDespachoDTO> buscar(@RequestParam Long id) {
        DetalleDespacho detalle = detalleDespachoServiceImpl.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrado con ID: " + id));

        return ResponseEntity.ok(new DetalleDespachoDTO(detalle));
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un detalle de despacho. Valida que el producto y el inventario existan como tambien la orden a la que se quiere agregar el detalle")
    public ResponseEntity<DetalleDespachoDTO> crear(@RequestBody DetalleDespachoDTO dto) {
        Producto producto = productoServiceImpl.buscarPorId(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Inventario inventario = inventarioServiceImpl.buscarPorId(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        if (dto.getCantidad() > inventario.getCantidad()) {
            throw new StockInsuficienteException("Cantidad insuficiente en inventario");
        }

        OrdenDespacho orden = ordenDespachoServiceImpl.buscarPorId(dto.getOrdenDespacho().getIdOrdenDespacho())
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

        DetalleDespacho detalle = new DetalleDespacho();
        detalle.setProducto(producto);
        detalle.setOrdenDespacho(orden);
        detalle.setCantidad(dto.getCantidad());

        inventarioServiceImpl.disminuirCantidad(detalle);
        detalleDespachoServiceImpl.crear(detalle);

        return ResponseEntity.status(HttpStatus.CREATED).body(new DetalleDespachoDTO(detalle));
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un detalle de despacho por el id tipo LONG")
    public ResponseEntity<DetalleDespachoDTO> actualizar(@RequestParam Long id, @RequestBody DetalleDespachoDTO dto) {
        DetalleDespacho detalle = detalleDespachoServiceImpl.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrado con ID: " + id));

        List<Inventario> inventarios = inventarioServiceImpl.buscarInventariosPorIdProducto(dto.getProducto().getIdProducto());

        if (inventarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No existe inventario con el producto " + dto.getProducto().getNombre());
        }

        Inventario inventario = inventarios.stream()
                .filter(invent -> invent.getCantidad() >= dto.getCantidad())
                .findFirst()
                .orElseThrow(() -> new StockInsuficienteException("Cantidad insuficiente en inventario"));

        detalle.setProducto(dto.getProducto());
        detalle.setCantidad(dto.getCantidad());

        detalleDespachoServiceImpl.crear(detalle);
        return ResponseEntity.ok(new DetalleDespachoDTO(detalle));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un detalle de una orden de despacho")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        if (!detalleDespachoServiceImpl.ExistePorId(id)) {
            throw new RecursoNoEncontradoException("Detalle de despacho con id " + id + " no encontrado");
        }
        detalleDespachoServiceImpl.eliminar(id);
        return ResponseEntity.ok("Detalle eliminado con éxito");
    }
}
