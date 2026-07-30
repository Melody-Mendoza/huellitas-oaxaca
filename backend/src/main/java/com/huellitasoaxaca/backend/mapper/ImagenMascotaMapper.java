package com.huellitasoaxaca.backend.mapper;

import com.huellitasoaxaca.backend.dto.response.ImagenMascotaResponse;
import com.huellitasoaxaca.backend.entity.ImagenMascota;

import org.springframework.stereotype.Component;

@Component
public class ImagenMascotaMapper 
{
    public ImagenMascotaResponse toResponse(ImagenMascota imagen) 
    {
        if (imagen == null) 
        {
            return null;
        }

        return new ImagenMascotaResponse(
                imagen.getId(),
                imagen.getUrl(),
                Boolean.TRUE.equals(imagen.getPrincipal())
        );
    }
}
