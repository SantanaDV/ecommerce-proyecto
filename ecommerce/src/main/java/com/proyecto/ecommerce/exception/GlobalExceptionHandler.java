package com.proyecto.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejador global de excepciones.
 * Captura las excepciones lanzadas en los controladores y devuelve respuestas uniformes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura y maneja las excepciones de tipo CustomException.
     *
     * @param ex Excepción lanzada en el sistema.
     * @return Respuesta con el mensaje de error y un código 400 BAD REQUEST.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> manejarCustomException(CustomException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Captura y maneja cualquier otra excepción no controlada.
     *
     * @param ex Excepción lanzada en el sistema.
     * @return Respuesta con un mensaje genérico de error interno y código 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarExcepcionGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error inesperado. Detalles: " + ex.getMessage());
    }
}