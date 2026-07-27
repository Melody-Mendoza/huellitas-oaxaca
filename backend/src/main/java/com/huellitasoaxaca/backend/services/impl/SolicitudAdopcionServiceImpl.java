package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.SolicitudAdopcionResponse;
import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.SolicitudAdopcionMapper;
import com.huellitasoaxaca.backend.repository.SolicitudAdopcionRepository;
import com.huellitasoaxaca.backend.services.SolicitudAdopcionService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService
{
    private final SolicitudAdopcionRepository solicitudRepository;
    private final SolicitudAdopcionMapper solicitudMapper;

    @Override
    public List<SolicitudAdopcionResponse> listarTodas() {
        return convertirLista(solicitudRepository.findAll());
    }

    @Override
    public SolicitudAdopcionResponse obtenerPorId(Long id) {
        SolicitudAdopcion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la solicitud con ID " + id
                        )
                );

        return solicitudMapper.toResponse(solicitud);
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorUsuario(Long usuarioId) {
        return convertirLista(
                solicitudRepository.findByUsuarioId(usuarioId)
        );
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorMascota(Long mascotaId) {
        return convertirLista(
                solicitudRepository.findByMascotaId(mascotaId)
        );
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorEstado(
            EstadoSolicitud estado
    ) {
        return convertirLista(
                solicitudRepository.findByEstado(estado)
        );
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorUsuarioYEstado(
            Long usuarioId,
            EstadoSolicitud estado
    ) {
        return convertirLista(
                solicitudRepository.findByUsuarioIdAndEstado(
                        usuarioId,
                        estado
                )
        );
    }

    private List<SolicitudAdopcionResponse> convertirLista(
            List<SolicitudAdopcion> solicitudes
    ) {
        return solicitudes.stream()
                .map(solicitudMapper::toResponse)
                .toList();
    }
}
