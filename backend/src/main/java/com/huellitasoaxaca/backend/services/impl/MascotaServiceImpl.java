package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.MascotaResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.MascotaMapper;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.services.MascotaService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MascotaServiceImpl implements MascotaService
{
    private final MascotaRepository mascotaRepository;
    private final MascotaMapper mascotaMapper;

    @Override
    public List<MascotaResponse> listarTodas() 
    {
        return convertirLista(mascotaRepository.findAll());
    }

    @Override
    public MascotaResponse obtenerPorId(Long id) 
    {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la mascota con ID " + id
                        )
                );

        return mascotaMapper.toResponse(mascota);
    }

    @Override
    public List<MascotaResponse> buscarPorNombre(String nombre) 
    {
        return convertirLista(
                mascotaRepository.findByNombreContainingIgnoreCase(nombre)
        );
    }

    @Override
    public List<MascotaResponse> listarPorRefugio(Long refugioId) 
    {
        return convertirLista(
                mascotaRepository.findByRefugioId(refugioId)
        );
    }

    @Override
    public List<MascotaResponse> listarPorEspecie(Especie especie) 
    {
        return convertirLista(
                mascotaRepository.findByEspecie(especie)
        );
    }

    @Override
    public List<MascotaResponse> listarPorEstado(EstadoMascota estado) 
    {
        return convertirLista(
                mascotaRepository.findByEstado(estado)
        );
    }

    @Override
    public List<MascotaResponse> listarPorEspecieYEstado(
            Especie especie,
            EstadoMascota estado
    ) 
    {
        return convertirLista(
                mascotaRepository.findByEspecieAndEstado(
                        especie,
                        estado
                )
        );
    }

    private List<MascotaResponse> convertirLista(
            List<Mascota> mascotas
    ) 
    {
        return mascotas.stream()
                .map(mascotaMapper::toResponse)
                .toList();
    }
}
