package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.SolicitudAdopcionResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudPropiaDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudPropiaResumenResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;
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

    public SolicitudPropiaResumenResponse toResumenPropio(
            SolicitudAdopcion solicitud
    )
    {
        Mascota mascota = solicitud.getMascota();
        Refugio refugio = mascota != null
                ? mascota.getRefugio()
                : null;

        return new SolicitudPropiaResumenResponse(
                solicitud.getId(),
                solicitud.getFechaSolicitud(),
                solicitud.getEstado(),
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null,
                mascota != null ? mascota.getImagen() : null,
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null
        );
    }

    public SolicitudPropiaDetalleResponse toDetallePropio(
            SolicitudAdopcion solicitud
    )
    {
        Mascota mascota = solicitud.getMascota();
        Refugio refugio = mascota != null
                ? mascota.getRefugio()
                : null;

        return new SolicitudPropiaDetalleResponse(
                solicitud.getId(),
                solicitud.getFechaSolicitud(),
                solicitud.getEstado(),
                solicitud.getComentarios(),
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null,
                mascota != null ? mascota.getImagen() : null,
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null
        );
    }
}
