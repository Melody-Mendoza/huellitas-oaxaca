package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.ImagenMascotaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagenMascotaService 
{
    ImagenMascotaResponse guardarPropia(
            Long refugioId,
            Long mascotaId,
            MultipartFile imagen,
            String correoAutenticado
    );

    List<ImagenMascotaResponse> listarPropias(
            Long refugioId,
            Long mascotaId,
            String correoAutenticado
    );

    ImagenMascotaResponse establecerPrincipalPropia(
            Long refugioId,
            Long mascotaId,
            Long imagenId,
            String correoAutenticado
    );

    void eliminarPropia(
            Long refugioId,
            Long mascotaId,
            Long imagenId,
            String correoAutenticado
    );

    List<ImagenMascotaResponse> listarPorMascota(Long mascotaId);

    ImagenMascotaResponse obtenerPorId(Long id);

    void eliminarPorMascota(Long mascotaId);
}
