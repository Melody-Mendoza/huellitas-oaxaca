package com.huellitasoaxaca.backend.exception;

public class SolicitudDuplicadaException extends RuntimeException
{
    public SolicitudDuplicadaException(String mensaje)
    {
        super(mensaje);
    }
}
