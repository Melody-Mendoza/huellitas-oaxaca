package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.ImagenMascotaResponse;

import java.util.List;

public interface ImagenMascotaService 
{
    List<ImagenMascotaResponse> listarPorMascota(Long mascotaId);

    ImagenMascotaResponse obtenerPorId(Long id);

    void eliminarPorMascota(Long mascotaId);
}
