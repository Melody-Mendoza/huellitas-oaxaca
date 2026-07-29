package com.huellitasoaxaca.backend.services.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.huellitasoaxaca.backend.services.CorreoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorreoServiceImpl implements CorreoService
{
    private final JavaMailSender mailSender;

    @Value("${app.password-recovery.url:}")
    private String urlRestablecimiento;

    @Value("${app.mail.from:}")
    private String remitente;

    @Override
    public void enviarRecuperacionPassword(
            String destinatario,
            String token
    )
    {
        if (urlRestablecimiento.isBlank() || remitente.isBlank())
        {
            throw new IllegalStateException(
                    "La recuperación por correo no está configurada"
            );
        }

        String separador = urlRestablecimiento.contains("?") ? "&" : "?";
        String enlace = urlRestablecimiento
                + separador
                + "token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Restablece tu contraseña de Huellitas Oaxaca");
        mensaje.setText("""
                Recibimos una solicitud para restablecer tu contraseña.

                Usa el siguiente enlace durante los próximos 30 minutos:
                %s

                Si no realizaste esta solicitud, ignora este correo.
                """.formatted(enlace));

        mailSender.send(mensaje);
    }
}
