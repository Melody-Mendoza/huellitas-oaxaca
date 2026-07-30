package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.FavoritoResponse;
import org.springframework.data.domain.Page;

public interface FavoritoService 
{
    FavoritoResponse agregar(Long mascotaId, String correoAutenticado);

    Page<FavoritoResponse> listar(
            String correoAutenticado,
            int page,
            int size
    );

    void eliminar(Long mascotaId, String correoAutenticado);
}
