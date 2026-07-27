package com.huellitasoaxaca.backend.mapper;

import com.huellitasoaxaca.backend.dto.response.ImagenMascotaResponse;
import com.huellitasoaxaca.backend.entity.ImagenMascota;
import com.huellitasoaxaca.backend.entity.Mascota;

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

        Mascota mascota = imagen.getMascota();

        return new ImagenMascotaResponse(
                imagen.getId(),
                imagen.getUrl(),
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null
        );
    }
}
