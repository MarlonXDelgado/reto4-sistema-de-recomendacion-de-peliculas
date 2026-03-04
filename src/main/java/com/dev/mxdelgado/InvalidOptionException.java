package com.dev.mxdelgado;

/**
 *Excepción lanzada cuando el usuario ingresa una opción fuera del rango permitido
 *en los menús del programa.
 *
 *Se utiliza principalmente en el método {@code getUserOption(...)} de {@link Main}
 *para forzar que el usuario vuelva a intentar hasta que ingrese un número válido.
 *
 *Extiende {@link RuntimeException} porque en este reto es una validación de
 *entrada (flujo esperado del programa) y no un error “crítico” que deba propagarse
 *obligatoriamente con {@code throws}.
 */

public class InvalidOptionException extends RuntimeException {

     /**
     * Crea la excepción con un mensaje por defecto.
     */
    public InvalidOptionException() {
        this("Opcion no valida seleccionada");
    }

     /**
     * Crea la excepción con un mensaje personalizado.
     * @param message mensaje descriptivo del error.
     */
    public InvalidOptionException(String message) {
        super(message);
    }
}
