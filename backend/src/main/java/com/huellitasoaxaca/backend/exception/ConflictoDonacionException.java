package com.huellitasoaxaca.backend.exception;

public class ConflictoDonacionException extends RuntimeException
{
    public ConflictoDonacionException(String mensaje)
    {
        super(mensaje);
    }
}
