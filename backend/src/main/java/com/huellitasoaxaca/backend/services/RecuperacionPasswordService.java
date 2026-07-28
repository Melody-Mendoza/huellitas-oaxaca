package com.huellitasoaxaca.backend.services;

public interface RecuperacionPasswordService 
{
    void solicitarRecuperacion(String correo);

    void restablecerPassword(
            String token,
            String nuevaPassword,
            String confirmarPassword
    );

    long eliminarTokensExpirados();
}
