package com.dev.mxdelgado;

public class DuplicateUserException extends Exception {

    public DuplicateUserException(){
        super("El nombre de usuario ya está registrado. Intente con otro nombre.");
    }

    public DuplicateUserException(String message){
        super(message);
    }

    public DuplicateUserException(String message, Throwable causa){
        super(message,causa);
    }
    
}
