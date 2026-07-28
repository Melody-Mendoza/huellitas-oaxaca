package com.huellitasoaxaca.backend.exception;

public class TokenRecuperacionInvalidoException extends RuntimeException
{
    public TokenRecuperacionInvalidoException(String mensaje) 
    {
        super(mensaje);
    }
}
