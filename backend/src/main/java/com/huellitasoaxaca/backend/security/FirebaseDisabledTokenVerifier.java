package com.huellitasoaxaca.backend.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.huellitasoaxaca.backend.exception.GoogleAuthenticationException;

@Service
@ConditionalOnProperty(
        name = "firebase.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class FirebaseDisabledTokenVerifier implements FirebaseTokenVerifier
{
    @Override
    public FirebaseIdentity verify(String idToken)
    {
        throw new GoogleAuthenticationException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El inicio de sesión con Google no está habilitado"
        );
    }
}
