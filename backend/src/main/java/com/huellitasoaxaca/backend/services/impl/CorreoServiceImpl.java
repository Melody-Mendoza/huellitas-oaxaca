package com.huellitasoaxaca.backend.services.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.huellitasoaxaca.backend.services.CorreoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorreoServiceImpl implements CorreoService
{
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String urlFrontend;

    @Value("${app.mail.from:}")
    private String remitente;

    @Override
    public void enviarRecuperacionPassword(
            String destinatario,
            String token
    )
    {
        if (urlFrontend.isBlank() || remitente.isBlank())
        {
            throw new IllegalStateException(
                    "La recuperación por correo no está configurada"
            );
        }

        String base = urlFrontend.trim().replaceAll("/+$", "");
        String enlace = base
                + "/restablecer-password?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String texto = """
                Hola,

                Recibimos una solicitud para restablecer tu contraseña de Huellitas Oaxaca.
                El enlace estará disponible durante los próximos 30 minutos.

                Restablece tu contraseña aquí:
                %s

                Si no realizaste esta solicitud, ignora este correo.

                Huellitas Oaxaca · Adopción responsable.
                """.formatted(enlace);
        String html = """
                <div style="margin:0;background:#f4f8f4;padding:32px 16px;font-family:Arial,Helvetica,sans-serif;color:#2e2e2e;">
                  <div style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #dfe7e0;border-radius:14px;overflow:hidden;">
                    <div style="background:#2f6e4f;padding:24px;text-align:center;color:#ffffff;">
                      <div style="font-size:22px;font-weight:700;">Huellitas Oaxaca</div>
                    </div>
                    <div style="padding:32px 28px;text-align:center;">
                      <h1 style="margin:0 0 16px;color:#2f6e4f;font-size:26px;line-height:1.2;">Restablece tu contraseña</h1>
                      <p style="margin:0 auto 24px;max-width:430px;font-size:16px;line-height:1.6;">Recibimos una solicitud para restablecer tu contraseña. Este enlace estará disponible durante los próximos 30 minutos.</p>
                      <a href="%s" style="display:inline-block;background:#2f6e4f;border-radius:8px;color:#ffffff;padding:13px 22px;text-decoration:none;font-weight:700;">Restablecer contraseña</a>
                      <p style="margin:26px 0 0;font-size:13px;line-height:1.5;color:#666666;">Si el botón no funciona, utiliza este enlace:</p>
                      <p style="margin:8px 0 0;word-break:break-all;font-size:13px;line-height:1.5;"><a href="%s" style="color:#2f6e4f;">%s</a></p>
                      <p style="margin:24px 0 0;font-size:13px;line-height:1.5;color:#666666;">Si no realizaste esta solicitud, ignora este correo.</p>
                    </div>
                    <div style="border-top:1px solid #edf1ed;padding:18px 24px;text-align:center;color:#718074;font-size:12px;">Huellitas Oaxaca · Adopción responsable.</div>
                  </div>
                </div>
                """.formatted(enlace, enlace, enlace);

        try
        {
            var mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mensaje,
                    true,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Restablece tu contraseña de Huellitas Oaxaca");
            helper.setText(texto, html);
            mailSender.send(mensaje);
        }
        catch (Exception exception)
        {
            throw new IllegalStateException(
                    "No fue posible preparar el correo de recuperación",
                    exception
            );
        }
    }
}
