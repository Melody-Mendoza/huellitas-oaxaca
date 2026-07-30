package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.DonacionResponse;
import com.huellitasoaxaca.backend.entity.Donacion;
import com.huellitasoaxaca.backend.entity.Refugio;

@Component
public class DonacionMapper 
{
    public DonacionResponse toResponse(Donacion donacion) 
    {
        if (donacion == null) 
        {
            return null;
        }

        Refugio refugio = donacion.getRefugio();

        return new DonacionResponse(
                 donacion.getId(),
                 donacion.getMonto(),
                 "MXN",
                 donacion.getMetodoPago(),
                 donacion.getFecha(),
                 donacion.getFechaActualizacion(),
                 donacion.getEstatus(),
                 donacion.getMensaje(),
                 refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null
        );
    }
}
