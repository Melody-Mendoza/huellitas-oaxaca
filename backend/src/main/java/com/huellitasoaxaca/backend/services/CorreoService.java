package com.huellitasoaxaca.backend.services;

public interface CorreoService
{
    void enviarRecuperacionPassword(String destinatario, String token);
}
