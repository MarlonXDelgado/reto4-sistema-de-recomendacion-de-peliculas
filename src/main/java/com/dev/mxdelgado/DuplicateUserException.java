package com.dev.mxdelgado;

/**
 * Excepción lanzada cuando se intenta crear un usuario duplicado.
 * Se lanza cuando se intenta realizar la creacion de un usuario identico gramaticalmente hablando.
 */

public class DuplicateUserException extends Exception {

    /**
     * Constructor por defecto.
     */
    public DuplicateUserException(){
        super("El nombre de usuario ya está registrado. Intente con otro nombre.");
    }

    /**
     * Constructor con mensaje personalizado.
     * @param message El mensaje de error.
     */
    public DuplicateUserException(String message){
        super(message);
    }

    /**
     * Constructor con mensaje y causa.
     * @param message El mensaje de error.
     * @param cause La causa de la excepción.
     */
    public DuplicateUserException(String message, Throwable causa){
        super(message,causa);
    }
    
}
