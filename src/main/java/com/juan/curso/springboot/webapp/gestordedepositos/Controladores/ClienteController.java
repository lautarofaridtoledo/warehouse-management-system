package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ClienteDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ClienteServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el controller real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.api.ClientController}.
 *
 * Nota: no se anota con @RestController para evitar endpoints duplicados.
 */
@Deprecated(forRemoval = true)
public class ClienteController {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.api.ClientController delegate;

    @Autowired
    public ClienteController(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.api.ClientController delegate) {
        this.delegate = delegate;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los clientes guardados en la base de datos")
    public ResponseEntity<?> buscarTodos() {
    return delegate.buscarTodos();
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca el cliente por ID de tipo Long")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        return delegate.buscar(id);
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un nuevo cliente")
    public ResponseEntity<?> crear(@RequestBody ClienteDTO dto) {
        return delegate.crear(dto);
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un cliente")
    public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody ClienteDTO dto) {
        return delegate.actualizar(id, dto);
    }


    @DeleteMapping("/eliminar")
    @Operation(summary = "Este medoto elimina un cliente de la base de datos por id tipo LONG")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        return delegate.eliminar(id);
    }
}
