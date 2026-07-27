package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.FavoritoResponse;

import java.util.List;

public interface FavoritoService 
{
    List<FavoritoResponse> listarPorUsuario(Long usuarioId);

    List<FavoritoResponse> listarPorMascota(Long mascotaId);

    boolean existe(Long usuarioId, Long mascotaId);

    void eliminar(Long usuarioId, Long mascotaId);
}
