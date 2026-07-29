package com.huellitasoaxaca.backend.services.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.entity.TokenRecuperacionPassword;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.TokenRecuperacionInvalidoException;
import com.huellitasoaxaca.backend.repository.TokenRecuperacionPasswordRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.CorreoService;
import com.huellitasoaxaca.backend.services.RecuperacionPasswordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecuperacionPasswordServiceImpl implements RecuperacionPasswordService
{
    private static final int MINUTOS_EXPIRACION = 30;
    private static final int BYTES_TOKEN = 32;
    private static final Logger log = LoggerFactory.getLogger(
            RecuperacionPasswordServiceImpl.class
    );
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionPasswordRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;

    @Override
    @Transactional
    public void solicitarRecuperacion(String correo) 
    {
        String correoNormalizado = correo
                .trim()
                .toLowerCase();

        usuarioRepository
                .findActivoPorCorreoParaActualizar(correoNormalizado)
                .filter(usuario -> usuario.getPassword() != null)
                .ifPresent(this::generarTokenYEnviarCorreo);
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
                        .findByTokenHashAndUtilizadoFalse(
                                calcularHash(token.trim())
                        )
                        .orElseThrow(() ->
                                new TokenRecuperacionInvalidoException(
                                        "El token de recuperación no es válido"
                                )
                        );

        if (recuperacion.estaExpirado()) 
        {
            throw new TokenRecuperacionInvalidoException(
                    "El token de recuperación no es válido"
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

    private void generarTokenYEnviarCorreo(Usuario usuario)
    {
        tokenRepository.deleteByUsuarioIdAndUtilizadoFalse(
                usuario.getId()
        );

        LocalDateTime ahora = LocalDateTime.now();
        String tokenPlano = generarTokenSeguro();

        TokenRecuperacionPassword token =
                TokenRecuperacionPassword.builder()
                        .tokenHash(calcularHash(tokenPlano))
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

        try
        {
            correoService.enviarRecuperacionPassword(
                    usuario.getCorreo(),
                    tokenPlano
            );
        }
        catch (RuntimeException exception)
        {
            tokenRepository.delete(token);
            log.warn(
                    "No fue posible enviar un correo de recuperación ({})",
                    exception.getClass().getSimpleName()
            );
        }
    }

    private String generarTokenSeguro()
    {
        byte[] bytes = new byte[BYTES_TOKEN];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String calcularHash(String token)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(
                    digest.digest(token.getBytes(StandardCharsets.UTF_8))
            );
        }
        catch (NoSuchAlgorithmException exception)
        {
            throw new IllegalStateException(
                    "SHA-256 no está disponible",
                    exception
            );
        }
    }
}
