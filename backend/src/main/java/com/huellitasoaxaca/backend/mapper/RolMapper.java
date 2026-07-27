package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.RolResponse;
import com.huellitasoaxaca.backend.entity.Rol;

@Component
public class RolMapper 
{
    public RolResponse toResponse(Rol rol) 
    {
        if (rol == null) 
        {
            return null;
        }

        return new RolResponse(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion()
        );
    }
}
