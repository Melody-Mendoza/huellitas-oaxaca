package com.huellitasoaxaca.backend.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioActivoValidator implements OAuth2TokenValidator<Jwt>
{
    private final UsuarioRepository usuarioRepository;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt)
    {
        String correo = jwt.getSubject();

        if (correo == null
                || correo.isBlank()
                || !usuarioRepository.existsByCorreoAndActivoTrue(
                        correo.trim().toLowerCase()
                ))
        {
            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "El usuario del token no está activo",
                    null
            );

            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
