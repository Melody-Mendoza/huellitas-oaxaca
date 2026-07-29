package com.huellitasoaxaca.backend.security;

import java.util.Locale;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.huellitasoaxaca.backend.exception.GoogleAuthenticationException;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(
        name = "firebase.enabled",
        havingValue = "true"
)
@RequiredArgsConstructor
public class FirebaseEnabledTokenVerifier implements FirebaseTokenVerifier
{
    private final FirebaseAuth firebaseAuth;

    @Override
    public FirebaseIdentity verify(String idToken)
    {
        try
        {
            FirebaseToken token = firebaseAuth.verifyIdToken(
                    idToken,
                    true
            );

            String correo = token.getEmail();

            if (correo == null || correo.isBlank())
            {
                throw unauthorized(
                        "El token no contiene un correo válido"
                );
            }

            if (!token.isEmailVerified())
            {
                throw new GoogleAuthenticationException(
                        HttpStatus.FORBIDDEN,
                        "La cuenta de Google no tiene el correo verificado"
                );
            }

            Object firebaseClaim = token.getClaims().get("firebase");

            if (!(firebaseClaim instanceof Map<?, ?> firebaseClaims)
                    || !"google.com".equals(
                            firebaseClaims.get("sign_in_provider")
                    ))
            {
                throw unauthorized(
                        "El token no corresponde al proveedor Google"
                );
            }

            return new FirebaseIdentity(
                    token.getUid(),
                    correo.trim().toLowerCase(Locale.ROOT),
                    token.getName()
            );
        }
        catch (GoogleAuthenticationException exception)
        {
            throw exception;
        }
        catch (FirebaseAuthException exception)
        {
            if (exception.getAuthErrorCode()
                    == AuthErrorCode.CERTIFICATE_FETCH_FAILED)
            {
                throw new GoogleAuthenticationException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "El servicio de autenticación con Google no está disponible"
                );
            }

            throw unauthorized("El Firebase ID token no es válido");
        }
    }

    private GoogleAuthenticationException unauthorized(String message)
    {
        return new GoogleAuthenticationException(
                HttpStatus.UNAUTHORIZED,
                message
        );
    }
}
