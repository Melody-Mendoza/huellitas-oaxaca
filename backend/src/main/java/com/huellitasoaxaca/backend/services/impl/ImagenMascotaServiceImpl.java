package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.ImagenMascotaResponse;
import com.huellitasoaxaca.backend.entity.ImagenMascota;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.ImagenMascotaMapper;
import com.huellitasoaxaca.backend.repository.ImagenMascotaRepository;
import com.huellitasoaxaca.backend.services.ImagenMascotaService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImagenMascotaServiceImpl implements ImagenMascotaService
{
    private final ImagenMascotaRepository imagenRepository;
    private final ImagenMascotaMapper imagenMapper;

    @Override
    public List<ImagenMascotaResponse> listarPorMascota(Long mascotaId) 
    {
        return imagenRepository.findByMascotaId(mascotaId)
                .stream()
                .map(imagenMapper::toResponse)
                .toList();
    }

    @Override
    public ImagenMascotaResponse obtenerPorId(Long id) 
    {
        ImagenMascota imagen = imagenRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la imagen con ID " + id
                        )
                );

        return imagenMapper.toResponse(imagen);
    }

    @Override
    @Transactional
    public void eliminarPorMascota(Long mascotaId) 
    {
        imagenRepository.deleteByMascotaId(mascotaId);
    }
}
