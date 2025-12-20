package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.api.dto.RolDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.application.RolServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/rol")
public class RolController {

    private final RolServiceImpl rolService;

    @Autowired
    public RolController(RolServiceImpl rolService) {
        this.rolService = rolService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este método obtiene todos los roles")
    public ResponseEntity<?> buscarTodos() {
        List<Rol> roles = rolService.buscarTodos()
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los roles"));

        List<RolDTO> dtoList = roles.stream()
            .map(RolDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
}