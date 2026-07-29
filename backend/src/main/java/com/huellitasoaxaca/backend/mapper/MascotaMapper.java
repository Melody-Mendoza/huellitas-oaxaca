package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.MascotaResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaCatalogoResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioCatalogoResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;

@Component
public class MascotaMapper 
{
    public MascotaCatalogoResponse toCatalogoResponse(Mascota mascota)
    {
        Refugio refugio = mascota.getRefugio();

        return new MascotaCatalogoResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getEdad(),
                mascota.getSexo(),
                mascota.getTamano(),
                mascota.getEstado(),
                mascota.getImagen(),
                new RefugioCatalogoResponse(
                        refugio.getId(),
                        refugio.getNombre(),
                        refugio.getDireccion()
                )
        );
    }

    public MascotaResponse toResponse(Mascota mascota) 
    {
        if (mascota == null) 
        {
            return null;
        }

        Refugio refugio = mascota.getRefugio();

        return new MascotaResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getSexo(),
                mascota.getEdad(),
                mascota.getPeso(),
                mascota.getTamano(),
                mascota.getDescripcion(),
                mascota.getEstado(),
                mascota.getFechaIngreso(),
                mascota.getImagen(),
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null
        );
    }
}
