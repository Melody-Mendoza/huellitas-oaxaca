package com.huellitasoaxaca.backend.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.services.TokenRevocadoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenRevocadoValidator  implements OAuth2TokenValidator<Jwt>
{
    private final TokenRevocadoService tokenRevocadoService;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String jti = jwt.getId();

        if (tokenRevocadoService.estaRevocado(jti)) {
            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "El token fue revocado",
                    null
            );

            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
