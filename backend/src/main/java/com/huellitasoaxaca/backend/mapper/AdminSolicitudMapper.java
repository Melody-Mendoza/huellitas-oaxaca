package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.SolicitudAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdminResumenResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;
import com.huellitasoaxaca.backend.entity.Usuario;

@Component
public class AdminSolicitudMapper
{
    public SolicitudAdminResumenResponse toAdminResumen(SolicitudAdopcion solicitud)
    {
        Usuario usuario = solicitud.getUsuario();
        Mascota mascota = solicitud.getMascota();
        return new SolicitudAdminResumenResponse(
                solicitud.getId(),
                solicitud.getFechaSolicitud(),
                solicitud.getEstado(),
                usuario != null ? usuario.getId() : null,
                usuario != null
                        ? usuario.getNombre() + " " + usuario.getApellidoPaterno()
                        : null,
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null
        );
    }

    public SolicitudAdminDetalleResponse toAdminDetalle(SolicitudAdopcion solicitud)
    {
        Usuario usuario = solicitud.getUsuario();
        Mascota mascota = solicitud.getMascota();
        Refugio refugio = mascota != null ? mascota.getRefugio() : null;
        return new SolicitudAdminDetalleResponse(
                solicitud.getId(),
                solicitud.getFechaSolicitud(),
                solicitud.getEstado(),
                solicitud.getComentarios(),
                usuario != null ? usuario.getId() : null,
                usuario != null
                        ? usuario.getNombre() + " " + usuario.getApellidoPaterno()
                        : null,
                usuario != null ? usuario.getCorreo() : null,
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null,
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null
        );
    }
}
