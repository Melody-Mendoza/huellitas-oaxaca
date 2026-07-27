package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.DonacionResponse;
import com.huellitasoaxaca.backend.entity.Donacion;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;

@Component
public class DonacionMapper 
{
    public DonacionResponse toResponse(Donacion donacion) 
    {
        if (donacion == null) 
        {
            return null;
        }

        Usuario usuario = donacion.getUsuario();
        Refugio refugio = donacion.getRefugio();

        return new DonacionResponse(
                donacion.getId(),
                donacion.getMonto(),
                donacion.getMetodoPago(),
                donacion.getFecha(),
                donacion.getEstatus(),
                donacion.getMensaje(),
                usuario != null ? usuario.getId() : null,
                usuario != null
                        ? usuario.getNombre() + " " + usuario.getApellidoPaterno()
                        : null,
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null
        );
    }
}
