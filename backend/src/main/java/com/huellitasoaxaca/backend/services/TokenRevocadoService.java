package com.huellitasoaxaca.backend.services;

import java.time.Instant;

public interface TokenRevocadoService 
{
    void revocar(
            String jti,
            String correo,
            Instant fechaExpiracion
    );

    boolean estaRevocado(String jti);

    long eliminarExpirados();
}
