package com.huellitasoaxaca.backend.exception;

public class FavoritoDuplicadoException extends RuntimeException
{
    public FavoritoDuplicadoException(String mensaje)
    {
        super(mensaje);
    }
}
