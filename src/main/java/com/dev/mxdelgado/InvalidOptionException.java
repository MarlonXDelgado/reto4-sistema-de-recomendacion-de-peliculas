package com.dev.mxdelgado;

public class InvalidOptionException extends RuntimeException {
    
    public InvalidOptionException() {
        this("Opcion no valida seleccionada");
    }
    
    public InvalidOptionException(String message) {
        super(message);
    }
}
