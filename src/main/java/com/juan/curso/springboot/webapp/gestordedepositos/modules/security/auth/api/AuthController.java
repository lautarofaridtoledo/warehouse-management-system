package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.application.AuthApplicationService;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.CambioDeClaveDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.CambioDeClaveResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/GestorDeDepositos")
public class AuthController {
    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authApplicationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }




    @PutMapping("/cambiarContrasenia")
    public ResponseEntity<?> cambiarContrasenia(@Valid @RequestBody CambioDeClaveDTO cambioDeClaveDTO){
        authApplicationService.cambiarContrasenia(cambioDeClaveDTO);
        return ResponseEntity.ok(new CambioDeClaveResponse("Contraseña actualizada correctamente"));
    }

}
