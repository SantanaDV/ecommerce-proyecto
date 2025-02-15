package com.proyecto.ecommerce.exception;


/**
 * Excepción personalizada para manejar errores específicos en la lógica de negocio.
 * Se usa en los servicios para lanzar errores cuando algo no cumple con las reglas del negocio.
 */
public class CustomException extends  RuntimeException {

    /**
     * Constructor que recibe un mensaje de error.
     *
     * @param mensaje Mensaje detallado del error.
     */
    public CustomException(String mensaje) {
        super(mensaje);
    }
}
