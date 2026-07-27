package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.SolicitudAdopcionResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;
import com.huellitasoaxaca.backend.entity.Usuario;

@Component
public class SolicitudAdopcionMapper 
{
    public SolicitudAdopcionResponse toResponse(
            SolicitudAdopcion solicitud
    ) 
    {
        if (solicitud == null) 
        {
            return null;
        }

        Usuario usuario = solicitud.getUsuario();
        Mascota mascota = solicitud.getMascota();

        return new SolicitudAdopcionResponse(
                solicitud.getId(),
                solicitud.getFechaSolicitud(),
                solicitud.getEstado(),
                solicitud.getComentarios(),
                usuario != null ? usuario.getId() : null,
                usuario != null
                        ? usuario.getNombre() + " " + usuario.getApellidoPaterno()
                        : null,
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null
        );
    }
}
