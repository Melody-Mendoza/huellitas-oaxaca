package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.FavoritoResponse;
import com.huellitasoaxaca.backend.entity.Favorito;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;

@Component
public class FavoritoMapper 
{
    public FavoritoResponse toResponse(Favorito favorito) 
    {
        if (favorito == null) 
        {
            return null;
        }

        Mascota mascota = favorito.getMascota();
        Refugio refugio = mascota != null ? mascota.getRefugio() : null;
        boolean disponibleParaAdopcion = mascota != null
                && mascota.getEstado() == EstadoMascota.DISPONIBLE
                && refugio != null
                && Boolean.TRUE.equals(refugio.getActivo());

        return new FavoritoResponse(
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null,
                mascota != null ? mascota.getEspecie() : null,
                mascota != null ? mascota.getRaza() : null,
                mascota != null ? mascota.getSexo() : null,
                mascota != null ? mascota.getEdad() : null,
                mascota != null ? mascota.getTamano() : null,
                mascota != null ? mascota.getEstado() : null,
                mascota != null ? mascota.getImagen() : null,
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null,
                disponibleParaAdopcion,
                favorito.getFechaAgregado()
        );
    }
}
