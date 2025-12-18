package com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.api;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ClienteDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.application.ClientServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/cliente")
public class ClientController {

    private final ClientServiceImpl clientService;

    @Autowired
    public ClientController(ClientServiceImpl clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los clientes guardados en la base de datos")
    public ResponseEntity<?> buscarTodos() {
        List<Cliente> clientes = clientService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los clientes"));

        List<ClienteDTO> dtoList = clientes.stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca el cliente por ID de tipo Long")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        Cliente cliente = clientService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));

        return ResponseEntity.ok(new ClienteDTO(cliente));
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un nuevo cliente")
    public ResponseEntity<?> crear(@RequestBody ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());

        Cliente creado = clientService.crear(cliente);
        return ResponseEntity.status(201).body(new ClienteDTO(creado));
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un cliente")
    public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody ClienteDTO dto) {
        Cliente cliente = clientService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));

        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());

        Cliente actualizado = clientService.actualizar(cliente);
        return ResponseEntity.ok(new ClienteDTO(actualizado));
    }


    @DeleteMapping("/eliminar")
    @Operation(summary = "Este medoto elimina un cliente de la base de datos por id tipo LONG")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        Optional<Cliente> cliente = clientService.buscarPorId(id);
        if (cliente.isPresent()) {
            clientService.eliminar(cliente.get().getIdCliente());
            return ResponseEntity.ok("Cliente eliminado con éxito");
        }
        throw new RecursoNoEncontradoException("Cliente no encontrado");
    }
}
