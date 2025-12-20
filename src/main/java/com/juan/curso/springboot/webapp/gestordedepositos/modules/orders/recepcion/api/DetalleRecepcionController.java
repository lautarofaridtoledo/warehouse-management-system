package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto.DetalleRecepcionBulkRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto.DetalleRecepcionItemDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.DetalleRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application.OrdenRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/detalleRecepcion")
public class DetalleRecepcionController {

    private final DetalleRecepcionServiceImpl detalleRecepcionService;
    private final OrdenRecepcionServiceImpl ordenRecepcionService;
    private final ProductoServiceImpl productoService;

    public DetalleRecepcionController(DetalleRecepcionServiceImpl detalleRecepcionService,
                                     OrdenRecepcionServiceImpl ordenRecepcionService,
                                     ProductoServiceImpl productoService) {
        this.detalleRecepcionService = detalleRecepcionService;
        this.ordenRecepcionService = ordenRecepcionService;
        this.productoService = productoService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los detalles de recepcion que se encuentran en la base de datos")
    public ResponseEntity<List<DetalleRecepcionDTO>> buscarTodos() {
        List<DetalleRecepcionDTO> detallesRecepcion = detalleRecepcionService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron detalles de recepción"))
                .stream()
                .map(DetalleRecepcionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(detallesRecepcion);
    }

    @GetMapping("/buscarDetallePorId")
    @Operation(summary = "Este metodo busca un detalle de recepcion por el id tipo LONG")
    public ResponseEntity<DetalleRecepcionDTO> buscarDetallePorIdDetalle(@RequestParam Long id) {
        DetalleRecepcion detalle = detalleRecepcionService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrado con ID: " + id));

        return ResponseEntity.ok(new DetalleRecepcionDTO(detalle));
    }

    @PostMapping("/crearDetalleRecepcion")
    @Operation(summary = "Este metodo crea un detalle de recepcion para una orden ya creada. Valida que exista la orden y que el producto tambien exista")
    public ResponseEntity<DetalleRecepcionDTO> crearDetalleRecepcion(@RequestBody DetalleRecepcionDTO dto) {
        Long idOrden = dto.getIdOrdenRecepcion();
        if (idOrden == null && dto.getOrden() != null) {
            idOrden = dto.getOrden().getIdOrdenRecepcion();
        }
        if (idOrden == null) {
            throw new IllegalArgumentException("Debe indicar el identificador de la orden asociada");
        }

        OrdenRecepcion orden = ordenRecepcionService.buscarPorId(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden asociada al detalle no encontrada"));

        Long productoId = dto.getProductoId();
        if (productoId == null) {
            throw new IllegalArgumentException("Debe indicar productoId");
        }

        productoService.buscarPorId(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + productoId));

        DetalleRecepcion detalleRecepcion = new DetalleRecepcion();
        detalleRecepcion.setOrdenRecepcion(orden);
    detalleRecepcion.setProductoId(productoId);
        detalleRecepcion.setCantidad(dto.getCantidad());

        detalleRecepcion = detalleRecepcionService.crear(detalleRecepcion);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DetalleRecepcionDTO(detalleRecepcion));
    }

    @PostMapping("/crearDetallesRecepcion")
    @Operation(summary = "Este metodo crea multiples detalles de recepcion para una orden existente")
    public ResponseEntity<List<DetalleRecepcionDTO>> crearDetallesRecepcion(@Valid @RequestBody DetalleRecepcionBulkRequest request) {
        Long idOrden = request.getIdOrdenRecepcion();
        if (idOrden == null) {
            idOrden = request.getDetalles().stream()
                    .map(DetalleRecepcionItemDTO::getIdOrdenRecepcion)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        if (idOrden == null) {
            throw new IllegalArgumentException("Debe indicar el identificador de la orden asociada");
        }

        OrdenRecepcion orden = ordenRecepcionService.buscarPorId(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden asociada al detalle no encontrada"));

        final Long idOrdenFinal = idOrden;
        List<DetalleRecepcion> detallesParaGuardar = new ArrayList<>();

        for (DetalleRecepcionItemDTO item : request.getDetalles()) {
            if (item.getIdOrdenRecepcion() != null && !Objects.equals(item.getIdOrdenRecepcion(), idOrdenFinal)) {
                throw new IllegalArgumentException("Todos los detalles deben pertenecer a la misma orden");
            }

            Long productoId = item.getProductoId();
            if (productoId == null) {
                throw new IllegalArgumentException("Debe indicar productoId");
            }
            productoService.buscarPorId(productoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + productoId));

            DetalleRecepcion detalle = new DetalleRecepcion();
            detalle.setOrdenRecepcion(orden);
            detalle.setProductoId(productoId);
            detalle.setCantidad(item.getCantidad());
            detallesParaGuardar.add(detalle);
        }

        List<DetalleRecepcionDTO> respuesta = detalleRecepcionService.crearTodos(detallesParaGuardar)
                .stream()
                .map(DetalleRecepcionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/actualizarDetalle")
    @Operation(summary = "Este metodo actualiza un detalle de recepcion de una orden ya creada")
    public ResponseEntity<DetalleRecepcionDTO> actualizarDetalleRecepcion(@RequestBody DetalleRecepcionDTO detalleDTO) {
        DetalleRecepcion detalle = detalleRecepcionService.buscarPorId(detalleDTO.getIdDetalleRecepcion())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalle no encontrado con ID: " + detalleDTO.getIdDetalleRecepcion()));

        detalle.setCantidad(detalleDTO.getCantidad());

        Long productoId = detalleDTO.getProductoId();
        if (productoId != null) {
            productoService.buscarPorId(productoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + productoId));
            detalle.setProductoId(productoId);
        }

        detalleRecepcionService.actualizar(detalle);
        return ResponseEntity.ok(new DetalleRecepcionDTO(detalle));
    }

    @DeleteMapping("/eliminarDetalleConIdDet")
    @Operation(summary = "Este metodo elimina un detalle de una orden ya creada")
    public ResponseEntity<String> eliminarDetalle(@RequestParam Long idDet) {
        detalleRecepcionService.eliminar(idDet);
        return ResponseEntity.ok("Detalle eliminado");
    }

    @DeleteMapping("/eliminarDetallesDeOrden")
    @Operation(summary = "Este metodo elimina TODOS los detalles de una orden")
    public ResponseEntity<String> eliminarDetallesDeOrden(@RequestParam Long idOrden) {
        List<DetalleRecepcion> detalles = detalleRecepcionService.buscarDetallesPorOrden(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalles no encontrados para la orden: " + idOrden));

        detalleRecepcionService.eliminarTodos(detalles);
        return ResponseEntity.ok("Detalles eliminados");
    }

    @GetMapping("/buscarDetallesPorIdOrden")
    @Operation(summary = "Este metodo busca todos los detalles por el id de orden tipo LONG")
    public ResponseEntity<List<DetalleRecepcionDTO>> buscarDetallesPorOrdenRecepcionId(@RequestParam Long idOrden) {
        List<DetalleRecepcion> detalles = detalleRecepcionService.buscarDetallesPorOrden(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontraron detalles para la orden: " + idOrden));

        List<DetalleRecepcionDTO> detallesDTO = detalles.stream()
                .map(DetalleRecepcionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(detallesDTO);
    }
}
