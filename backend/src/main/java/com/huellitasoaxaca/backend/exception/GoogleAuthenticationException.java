package com.huellitasoaxaca.backend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class GoogleAuthenticationException extends RuntimeException
{
    private final HttpStatus status;

    public GoogleAuthenticationException(
            HttpStatus status,
            String message
    )
    {
        super(message);
        this.status = status;
    }
}
