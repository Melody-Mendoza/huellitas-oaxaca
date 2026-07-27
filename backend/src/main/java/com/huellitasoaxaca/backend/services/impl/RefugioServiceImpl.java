package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.RefugioResponse;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.RefugioMapper;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.services.RefugioService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefugioServiceImpl implements RefugioService
{
    private final RefugioRepository refugioRepository;
    private final RefugioMapper refugioMapper;

    @Override
    public List<RefugioResponse> listarTodos() 
    {
        return refugioRepository.findAll()
                .stream()
                .map(refugioMapper::toResponse)
                .toList();
    }

    @Override
    public List<RefugioResponse> listarActivos() 
    {
        return refugioRepository.findByActivoTrue()
                .stream()
                .map(refugioMapper::toResponse)
                .toList();
    }

    @Override
    public RefugioResponse obtenerPorId(Long id) {
        return refugioMapper.toResponse(buscarEntidadPorId(id));
    }

    @Override
    public RefugioResponse obtenerPorNombre(String nombre) 
    {
        Refugio refugio = refugioRepository.findByNombre(nombre)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el refugio " + nombre
                        )
                );

        return refugioMapper.toResponse(refugio);
    }

    @Override
    public List<RefugioResponse> listarPorUsuario(Long usuarioId) 
    {
        List<Refugio> refugios = refugioRepository.findByUsuarioId(usuarioId);

        if (refugios.isEmpty()) 
        {
            throw new RecursoNoEncontradoException(
                    "El usuario no tiene refugios asignados"
            );
        }

        return refugios.stream()
                .map(refugioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void desactivar(Long id) 
    {
        Refugio refugio = buscarEntidadPorId(id);

        refugio.setActivo(false);
        refugioRepository.save(refugio);
    }

    private Refugio buscarEntidadPorId(Long id) 
    {
        return refugioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el refugio con ID " + id
                        )
                );
    }
}
