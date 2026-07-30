package com.huellitasoaxaca.backend.exception;

import org.springframework.http.HttpStatus;

public class ImagenMascotaException extends RuntimeException
{
    private final HttpStatus status;

    public ImagenMascotaException(HttpStatus status, String message)
    {
        super(message);
        this.status = status;
    }

    public ImagenMascotaException(
            HttpStatus status,
            String message,
            Throwable cause
    )
    {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus()
    {
        return status;
    }
}
