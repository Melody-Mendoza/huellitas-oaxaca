package com.huellitasoaxaca.backend.services.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.entity.TokenRecuperacionPassword;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.TokenRecuperacionInvalidoException;
import com.huellitasoaxaca.backend.repository.TokenRecuperacionPasswordRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.RecuperacionPasswordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecuperacionPasswordServiceImpl implements RecuperacionPasswordService
{
    private static final int MINUTOS_EXPIRACION = 30;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionPasswordRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void solicitarRecuperacion(String correo) 
    {
        String correoNormalizado = correo
                .trim()
                .toLowerCase();

        usuarioRepository
                .findByCorreoAndActivoTrue(correoNormalizado)
                .ifPresent(this::generarToken);
    }

    @Override
    @Transactional
    public void restablecerPassword(
            String token,
            String nuevaPassword,
            String confirmarPassword
    ) 
    {
        if (!nuevaPassword.equals(confirmarPassword)) 
        {
            throw new TokenRecuperacionInvalidoException(
                    "Las contraseñas no coinciden"
            );
        }

        TokenRecuperacionPassword recuperacion =
                tokenRepository
                        .findByTokenAndUtilizadoFalse(token)
                        .orElseThrow(() ->
                                new TokenRecuperacionInvalidoException(
                                        "El token de recuperación no es válido"
                                )
                        );

        if (recuperacion.estaExpirado()) 
        {
            throw new TokenRecuperacionInvalidoException(
                    "El token de recuperación ha expirado"
            );
        }

        Usuario usuario = recuperacion.getUsuario();

        usuario.setPassword(
                passwordEncoder.encode(nuevaPassword)
        );

        recuperacion.setUtilizado(true);

        usuarioRepository.save(usuario);
        tokenRepository.save(recuperacion);
    }

    @Override
    @Transactional
    public long eliminarTokensExpirados() 
    {
        return tokenRepository
                .deleteByFechaExpiracionBefore(
                        LocalDateTime.now()
                );
    }

    private void generarToken(Usuario usuario) 
    {
        tokenRepository.deleteByUsuarioIdAndUtilizadoFalse(
                usuario.getId()
        );

        LocalDateTime ahora = LocalDateTime.now();

        TokenRecuperacionPassword token =
                TokenRecuperacionPassword.builder()
                        .token(UUID.randomUUID().toString())
                        .usuario(usuario)
                        .fechaCreacion(ahora)
                        .fechaExpiracion(
                                ahora.plusMinutes(
                                        MINUTOS_EXPIRACION
                                )
                        )
                        .utilizado(false)
                        .build();

        tokenRepository.save(token);
    }
}
