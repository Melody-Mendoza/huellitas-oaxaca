package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.HistorialEstadoResponse;
import com.huellitasoaxaca.backend.entity.HistorialEstado;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.HistorialEstadoMapper;
import com.huellitasoaxaca.backend.repository.HistorialEstadoRepository;
import com.huellitasoaxaca.backend.services.HistorialEstadoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistorialEstadoServiceImpl implements HistorialEstadoService
{
    private final HistorialEstadoRepository historialRepository;
    private final HistorialEstadoMapper historialMapper;

    @Override
    public List<HistorialEstadoResponse> listarPorSolicitud(
            Long solicitudId
    ) 
    {
        return historialRepository
                .findBySolicitudIdOrderByFechaAsc(solicitudId)
                .stream()
                .map(historialMapper::toResponse)
                .toList();
    }

    @Override
    public List<HistorialEstadoResponse> listarPorEstado(
            EstadoSolicitud estado
    ) 
    {
        return historialRepository.findByEstado(estado)
                .stream()
                .map(historialMapper::toResponse)
                .toList();
    }

    @Override
    public HistorialEstadoResponse obtenerPorId(Long id) 
    {
        HistorialEstado historial = historialRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el historial con ID " + id
                        )
                );

        return historialMapper.toResponse(historial);
    }
}
