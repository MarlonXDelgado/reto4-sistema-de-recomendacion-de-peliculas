package com.dev.mxdelgado;

/**
 * Excepción lanzada cuando no se encuentra una película.
 * Se lanza cuando la búsqueda no retorna resultados o la película no existe.
 */
public class MovieNotFoundException extends Exception {
    
    /**
     * Constructor por defecto.
     */
    public MovieNotFoundException() {
        super("No se encontró la película solicitada.");
    }
    
    /**
     * Constructor con mensaje personalizado.
     * @param message El mensaje de error.
     */
    public MovieNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * @param message El mensaje de error.
     * @param cause La causa de la excepción.
     */
    public MovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
