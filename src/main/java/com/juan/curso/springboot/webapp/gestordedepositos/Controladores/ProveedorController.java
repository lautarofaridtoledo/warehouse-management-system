package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProveedorDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProveedorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Deprecated(forRemoval = true)
public class ProveedorController {

    private final ProveedorServiceImpl proveedorService;

    @Autowired
    public ProveedorController(ProveedorServiceImpl proveedorService) {
        this.proveedorService = proveedorService;
    }

    public ResponseEntity<List<ProveedorDTO>> buscarTodos() {
        List<ProveedorDTO> dtoList = proveedorService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los proveedores"))
                .stream()
                .map(ProveedorDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }


    public ResponseEntity<ProveedorDTO> buscar(Long id) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id));

        return ResponseEntity.ok(new ProveedorDTO(proveedor));
    }

    
    public ResponseEntity<ProveedorDTO> crear(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.crear(proveedor);

        return new ResponseEntity<>(new ProveedorDTO(proveedor), HttpStatus.CREATED);
    }

    
    public ResponseEntity<ProveedorDTO> actualizar(Long id, ProveedorDTO dto) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));

        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.actualizar(proveedor);

        return ResponseEntity.ok(new ProveedorDTO(proveedor));
    }

    
    public ResponseEntity<String> eliminar(Long id) {
        proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id));
        
        proveedorService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado con éxito");
    }
}
