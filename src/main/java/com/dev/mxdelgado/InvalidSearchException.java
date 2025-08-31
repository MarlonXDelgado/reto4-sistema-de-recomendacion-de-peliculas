package com.dev.mxdelgado;

/**
 * Excepción lanzada cuando la entrada de búsqueda es inválida.
 * Se lanza cuando la búsqueda está vacía, es muy corta o contiene caracteres inválidos.
 */
public class InvalidSearchException extends Exception {
    
    /**
     * Constructor por defecto.
     */
    public InvalidSearchException() {
        super("La entrada de búsqueda es inválida.");
    }
    
    /**
     * Constructor con mensaje personalizado.
     * @param message El mensaje de error.
     */
    public InvalidSearchException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * @param message El mensaje de error.
     * @param cause La causa de la excepción.
     */
    public InvalidSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
