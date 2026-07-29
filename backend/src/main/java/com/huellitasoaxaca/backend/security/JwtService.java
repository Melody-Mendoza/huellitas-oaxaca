package com.huellitasoaxaca.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.huellitasoaxaca.backend.entity.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService 
{
        private final JwtEncoder jwtEncoder;

        @Value("${security.jwt.expiration-minutes}")
        private long expirationMinutes;

        @Value("${security.jwt.issuer}")
        private String issuer;

        public String generarToken(UserDetails userDetails)
        {
                List<String> roles = userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

                return generarToken(
                        userDetails.getUsername(),
                        roles
                );
        }

        public String generarToken(Usuario usuario)
        {
                return generarToken(
                        usuario.getCorreo(),
                        List.of(
                                "ROLE_" + usuario.getRol().getNombre()
                        )
                );
        }

        private String generarToken(
                String correo,
                List<String> roles
        )
        {
                Instant ahora = Instant.now();
                Instant expiracion = ahora.plus(
                        expirationMinutes,
                        ChronoUnit.MINUTES
                );

                String jti = UUID.randomUUID().toString();

                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .id(jti)
                        .issuer(issuer)
                        .issuedAt(ahora)
                        .expiresAt(expiracion)
                        .subject(correo)
                        .claim("roles", roles)
                        .build();

                JwsHeader header = JwsHeader
                        .with(MacAlgorithm.HS256)
                        .build();

                return jwtEncoder
                        .encode(
                                JwtEncoderParameters.from(header, claims)
                        )
                        .getTokenValue();
        }

        public long obtenerExpiracionEnSegundos() 
        {
                return expirationMinutes * 60;
        }
}
