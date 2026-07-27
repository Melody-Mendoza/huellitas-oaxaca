package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.FavoritoResponse;
import com.huellitasoaxaca.backend.entity.Favorito;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Usuario;

@Component
public class FavoritoMapper 
{
    public FavoritoResponse toResponse(Favorito favorito) 
    {
        if (favorito == null) 
        {
            return null;
        }

        Usuario usuario = favorito.getUsuario();
        Mascota mascota = favorito.getMascota();

        return new FavoritoResponse(
                usuario != null ? usuario.getId() : null,
                usuario != null
                        ? usuario.getNombre() + " " + usuario.getApellidoPaterno()
                        : null,
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null
        );
    }
}
