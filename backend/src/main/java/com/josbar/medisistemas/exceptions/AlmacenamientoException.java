package com.josbar.medisistemas.exceptions;

/**
 * El almacenamiento de archivos (Supabase Storage) no está configurado o no respondió.
 */
public class AlmacenamientoException extends RuntimeException {

    public AlmacenamientoException(String message) {
        super(message);
    }

    public AlmacenamientoException(String message, Throwable cause) {
        super(message, cause);
    }
}
