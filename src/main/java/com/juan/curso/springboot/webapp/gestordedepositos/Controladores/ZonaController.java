package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ZonaDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ZonaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/zona")
public class ZonaController {
    private final ZonaServiceImpl zonaService;

    @Autowired
    public ZonaController(ZonaServiceImpl zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todas las zonas")
    public ResponseEntity<List<ZonaDTO>> buscarTodos() {
        List<Zona> zonas = zonaService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron las zonas"));

        List<ZonaDTO> dtoList = zonas.stream()
                .map(ZonaDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca una zona por su id")
    public ResponseEntity<ZonaDTO> buscar(@RequestParam Long id) {
        Zona zona = zonaService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Zona no encontrada con ID: " + id));

        return ResponseEntity.ok(new ZonaDTO(zona));
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea una nueva zona")
    public ResponseEntity<ZonaDTO> crear(@RequestBody ZonaDTO dto) {
        Zona zona = new Zona();
        zona.setNombre(dto.getNombre());
        zona.setDescripcion(dto.getDescripcion());
        zona.setCategoriasAdmitidas(dto.getCategoriasAdmitidas());

        zonaService.crear(zona);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ZonaDTO(zona));
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza una zona")
    public ResponseEntity<ZonaDTO> actualizar(@RequestParam Long id, @RequestBody ZonaDTO dto) {
        Zona zona = zonaService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Zona no encontrada con id: " + id));

        zona.setNombre(dto.getNombre());
        zona.setDescripcion(dto.getDescripcion());
        zona.setCategoriasAdmitidas(dto.getCategoriasAdmitidas());

        zonaService.actualizar(zona);

        return ResponseEntity.ok(new ZonaDTO(zona));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina una zona")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        zonaService.eliminar(id);
        return ResponseEntity.ok("Zona eliminada con éxito");
    }
}
