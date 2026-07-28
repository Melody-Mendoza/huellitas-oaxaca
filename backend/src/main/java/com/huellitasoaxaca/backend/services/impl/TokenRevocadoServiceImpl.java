package com.huellitasoaxaca.backend.services.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.entity.TokenRevocado;
import com.huellitasoaxaca.backend.repository.TokenRevocadoRepository;
import com.huellitasoaxaca.backend.services.TokenRevocadoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenRevocadoServiceImpl implements TokenRevocadoService
{
    private final TokenRevocadoRepository tokenRevocadoRepository;

    @Override
    @Transactional
    public void revocar(
            String jti,
            String correo,
            Instant fechaExpiracion
    ) 
    {
        if (tokenRevocadoRepository.existsByJti(jti)) {
            return;
        }

        TokenRevocado tokenRevocado = TokenRevocado.builder()
                .jti(jti)
                .correoUsuario(correo)
                .fechaRevocacion(LocalDateTime.now())
                .fechaExpiracion(
                        LocalDateTime.ofInstant(
                                fechaExpiracion,
                                ZoneId.systemDefault()
                        )
                )
                .build();

        tokenRevocadoRepository.save(tokenRevocado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaRevocado(String jti) 
    {
        if (jti == null || jti.isBlank()) 
        {
            return false;
        }

        return tokenRevocadoRepository.existsByJti(jti);
    }

    @Override
    @Transactional
    public long eliminarExpirados() {
    
        return tokenRevocadoRepository
                .deleteByFechaExpiracionBefore(
                        LocalDateTime.now()
                );
    }
}
