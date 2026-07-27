package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.HistorialEstadoResponse;
import com.huellitasoaxaca.backend.entity.HistorialEstado;
import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;

@Component
public class HistorialEstadoMapper 
{
    public HistorialEstadoResponse toResponse(HistorialEstado historial) 
    {
        if (historial == null) 
        {
            return null;
        }

        SolicitudAdopcion solicitud = historial.getSolicitud();

        return new HistorialEstadoResponse(
                historial.getId(),
                solicitud != null ? solicitud.getId() : null,
                historial.getEstado(),
                historial.getFecha(),
                historial.getObservaciones()
        );
    }
}
