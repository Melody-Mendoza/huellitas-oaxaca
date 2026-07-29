package com.huellitasoaxaca.backend.exception;

public class ParametroInvalidoException extends RuntimeException
{
    public ParametroInvalidoException(String mensaje)
    {
        super(mensaje);
    }
}
