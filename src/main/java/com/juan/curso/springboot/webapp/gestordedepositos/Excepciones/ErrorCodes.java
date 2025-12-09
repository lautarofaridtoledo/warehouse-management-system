package com.juan.curso.springboot.webapp.gestordedepositos.Excepciones;

/**
 * Constantes centralizadas para códigos y mensajes de error.
 * 
 * Uso:
 * - Los CÓDIGOS se usan en ErrorResponse para identificar el tipo de error
 * - Los MENSAJES son los textos que ve el usuario/cliente
 * 
 * Convención:
 * - Códigos: UPPER_SNAKE_CASE descriptivo
 * - Mensajes: Texto amigable en español
 */
public final class ErrorCodes {

    private ErrorCodes() {
        // Evita instanciación
    }

    // ==================== CÓDIGOS DE ERROR ====================

    // --- Recursos ---
    public static final String RECURSO_NO_ENCONTRADO = "RECURSO_NO_ENCONTRADO";

    // --- Stock/Inventario ---
    public static final String STOCK_INSUFICIENTE = "STOCK_INSUFICIENTE";
    public static final String CAPACIDAD_EXCEDIDA = "CAPACIDAD_EXCEDIDA";

    // --- Validación ---
    public static final String ARGUMENTO_INVALIDO = "ARGUMENTO_INVALIDO";
    public static final String VALIDACION_FALLIDA = "VALIDACION_FALLIDA";
    public static final String ESTADO_INVALIDO = "ESTADO_INVALIDO";

    // --- Base de datos ---
    public static final String INTEGRIDAD_DATOS = "INTEGRIDAD_DATOS";

    // --- Seguridad ---
    public static final String ACCESO_DENEGADO = "ACCESO_DENEGADO";

    // --- Errores genéricos ---
    public static final String ERROR_INTERNO = "ERROR_INTERNO";
    public static final String ERROR_INESPERADO = "ERROR_INESPERADO";

    // ==================== MENSAJES DE ERROR ====================

    // --- Mensajes genéricos reutilizables ---
    public static final String MSG_ERROR_INTERNO = "Ocurrió un error interno. Por favor, intente nuevamente.";
    public static final String MSG_ERROR_INESPERADO = "Ocurrió un error inesperado. Por favor, contacte al administrador.";
    public static final String MSG_ACCESO_DENEGADO = "No tiene permisos para realizar esta operación.";
    
    // --- Mensajes de integridad de datos ---
    public static final String MSG_INTEGRIDAD_GENERICA = "No se puede completar la operación por restricciones de datos.";
    public static final String MSG_RECURSO_ASOCIADO = "El recurso está asociado a otros registros y no puede eliminarse.";
    public static final String MSG_REGISTRO_DUPLICADO = "Ya existe un registro con esos datos.";
}
