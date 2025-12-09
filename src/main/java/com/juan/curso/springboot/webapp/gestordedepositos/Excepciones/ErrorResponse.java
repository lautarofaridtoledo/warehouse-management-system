package com.juan.curso.springboot.webapp.gestordedepositos.Excepciones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO estándar para respuestas de error de la API.
 * Garantiza consistencia en el formato de errores.
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    
    private String codigo;
    private String mensaje;
    private LocalDateTime timestamp;

    public ErrorResponse(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }
}
