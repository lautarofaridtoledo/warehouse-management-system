package com.juan.curso.springboot.webapp.gestordedepositos.modules.proveedores.api;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProveedorDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.proveedores.application.ProveedorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/proveedor")
public class ProveedorController {

    private final ProveedorServiceImpl proveedorService;

    @Autowired
    public ProveedorController(ProveedorServiceImpl proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los proveedores")
    public ResponseEntity<List<ProveedorDTO>> buscarTodos() {
        List<ProveedorDTO> dtoList = proveedorService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los proveedores"))
                .stream()
                .map(ProveedorDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca un proveedor por su id")
    public ResponseEntity<ProveedorDTO> buscar(@RequestParam Long id) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id));

        return ResponseEntity.ok(new ProveedorDTO(proveedor));
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un nuevo proveedor")
    public ResponseEntity<ProveedorDTO> crear(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.crear(proveedor);

        return new ResponseEntity<>(new ProveedorDTO(proveedor), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un proveedor")
    public ResponseEntity<ProveedorDTO> actualizar(@RequestParam Long id, @RequestBody ProveedorDTO dto) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));

        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.actualizar(proveedor);

        return ResponseEntity.ok(new ProveedorDTO(proveedor));
    }

    @DeleteMapping("eliminar")
    @Operation(summary = "Este metodo elimina un proveedor por su id")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id));
        
        proveedorService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado con éxito");
    }
}
