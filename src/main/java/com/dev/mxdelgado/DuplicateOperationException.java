package com.dev.mxdelgado;

/**
 * Excepción lanzada cuando se intenta realizar una operación duplicada.
 * Se lanza cuando se intenta marcar como vista una película ya vista,
 * o cuando se intenta puntuar una película ya puntuada.
 */
public class DuplicateOperationException extends Exception {
    
    /**
     * Constructor por defecto.
     */
    public DuplicateOperationException() {
        super("La operación ya ha sido realizada anteriormente.");
    }
    
    /**
     * Constructor con mensaje personalizado.
     * @param message El mensaje de error.
     */
    public DuplicateOperationException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * @param message El mensaje de error.
     * @param cause La causa de la excepción.
     */
    public DuplicateOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
