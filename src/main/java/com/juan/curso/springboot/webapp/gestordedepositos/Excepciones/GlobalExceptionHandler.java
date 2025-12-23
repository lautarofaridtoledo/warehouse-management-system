package com.juan.curso.springboot.webapp.gestordedepositos.Excepciones;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.ErrorCodes.*;

/**
 * Manejador global de excepciones para toda la API.
 * Centraliza el manejo de errores y garantiza respuestas consistentes.
 * 
 * Beneficios:
 * - Elimina try-catch repetitivos en controllers
 * - Formato de error uniforme (ErrorResponse)
 * - Punto único para logging/métricas de errores
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja credenciales inválidas de autenticación (401 Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(AUTENTICACION_FALLIDA, MSG_AUTENTICACION_FALLIDA);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Maneja recursos no encontrados (404 Not Found)
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorResponse error = new ErrorResponse(RECURSO_NO_ENCONTRADO, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja stock insuficiente (409 Conflict)
     */
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex) {
        ErrorResponse error = new ErrorResponse(STOCK_INSUFICIENTE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja capacidad excedida en ubicaciones (409 Conflict)
     */
    @ExceptionHandler(CapacidadExcedida.class)
    public ResponseEntity<ErrorResponse> handleCapacidadExcedida(CapacidadExcedida ex) {
        ErrorResponse error = new ErrorResponse(CAPACIDAD_EXCEDIDA, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja argumentos inválidos (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(ARGUMENTO_INVALIDO, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de validación de @Valid (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String mensajes = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(VALIDACION_FALLIDA, mensajes);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de estado inválido (400 Bad Request)
     * Ej: intentar editar una orden COMPLETADA
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse error = new ErrorResponse(ESTADO_INVALIDO, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja violaciones de integridad de datos (FK constraints, unique, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensaje = MSG_INTEGRIDAD_GENERICA;
        
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String causa = ex.getCause().getMessage().toLowerCase();
            if (causa.contains("foreign key") || causa.contains("referenced")) {
                mensaje = MSG_RECURSO_ASOCIADO;
            } else if (causa.contains("unique") || causa.contains("duplicate")) {
                mensaje = MSG_REGISTRO_DUPLICADO;
            }
        }
        
        ErrorResponse error = new ErrorResponse(INTEGRIDAD_DATOS, mensaje);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja acceso denegado (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(ACCESO_DENEGADO, MSG_ACCESO_DENEGADO);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Maneja cualquier RuntimeException no capturada específicamente (500)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse(ERROR_INTERNO, MSG_ERROR_INTERNO);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Fallback para cualquier excepción no manejada (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse(ERROR_INESPERADO, MSG_ERROR_INESPERADO);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
