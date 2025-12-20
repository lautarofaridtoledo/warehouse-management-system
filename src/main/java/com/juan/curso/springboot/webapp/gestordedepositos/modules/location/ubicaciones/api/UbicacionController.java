package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.api.dto.ReporteUbicacionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.api.dto.UbicacionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.UbicacionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.application.ZonaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ubicacion")
public class UbicacionController {

    private final UbicacionServiceImpl ubicacionService;
    private final ZonaServiceImpl zonaService;
    private final InventarioServiceImpl inventarioService;

    public UbicacionController(UbicacionServiceImpl ubicacionService,
                              ZonaServiceImpl zonaService,
                              InventarioServiceImpl inventarioService) {
        this.ubicacionService = ubicacionService;
        this.zonaService = zonaService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todas las ubicaciones")
    public ResponseEntity<?> buscarTodos() {
        List<UbicacionDTO> ubicaciones = ubicacionService.buscarTodos().orElseThrow().stream()
                .map(UbicacionDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ubicaciones, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca una ubicacion por su id")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        Ubicacion ubicacion = ubicacionService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Ubicacion no encontrada con ID: " + id));
        return new ResponseEntity<>(new UbicacionDTO(ubicacion), HttpStatus.OK);
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea una nueva ubicacion")
    public ResponseEntity<?> crear(@RequestBody UbicacionDTO dto) {
        Zona zona = zonaService.buscarPorId(dto.getZona().getIdZona())
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + dto.getZona().getIdZona()));

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setCodigo(dto.getCodigo());
        ubicacion.setZona(zona);
        ubicacion.setCapacidadMaxima(dto.getCapacidadMaxima());
        ubicacion.setOcupadoActual(dto.getOcupadoActual());
        ubicacion = ubicacionService.crear(ubicacion);
        return new ResponseEntity<>(new UbicacionDTO(ubicacion), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo busca todas las ubicaciones")
    public ResponseEntity<?> actualizar(@RequestBody UbicacionDTO dto, @RequestParam Long id) throws RecursoNoEncontradoException {
        Optional<Ubicacion> existente = ubicacionService.buscarPorId(id);

        if (existente.isPresent()) {
            Ubicacion ubicacion = existente.get();

            ubicacion.setCodigo(dto.getCodigo());
            ubicacion.setZona(dto.getZona());
            ubicacion.setCapacidadMaxima(dto.getCapacidadMaxima());
            ubicacion.setOcupadoActual(dto.getOcupadoActual());

            ubicacion = ubicacionService.actualizar(ubicacion);

            return ResponseEntity.ok(new UbicacionDTO(ubicacion));
        } else {
            throw new RecursoNoEncontradoException("No se encontró el ubicacion con ID: " + id);
        }
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina una ubicacion")
    public ResponseEntity<?> eliminar(@RequestParam Long id) throws Exception {
        Optional<Ubicacion> ubicacionEncontrada = ubicacionService.buscarPorId(id);
        if (ubicacionEncontrada.isPresent()) {
            List<Inventario> inventariosConEsaUbicacion = inventarioService.buscarTodos().get().stream().filter(
                    inventario -> Objects.equals(inventario.getUbicacion().getIdUbicacion(), ubicacionEncontrada.get().getIdUbicacion())
            ).collect(Collectors.toList());
            if (inventariosConEsaUbicacion.size() > 0) {
                throw new Exception("No se puede eliminar la ubicacion porque esta asignada a un inventario");
            } else {
                ubicacionService.eliminar(id);
            }
        }
        return ResponseEntity.ok("Ubicacion eliminada con éxito");
    }

    @GetMapping("/reporte")
    @Operation(summary = "Este metodo reporta el lugar disponible en una ubicacion")
    public ResponseEntity<List<ReporteUbicacionDTO>> listarEspacioUbicaciones() {
        List<ReporteUbicacionDTO> lista = ubicacionService.obtenerEspacioDeUbicaciones();
        return ResponseEntity.ok(lista);
    }
}
