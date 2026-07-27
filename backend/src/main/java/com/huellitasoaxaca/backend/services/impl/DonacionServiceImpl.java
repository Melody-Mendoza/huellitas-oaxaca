package com.huellitasoaxaca.backend.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.DonacionResponse;
import com.huellitasoaxaca.backend.entity.Donacion;
import com.huellitasoaxaca.backend.entity.enums.EstatusDonacion;
import com.huellitasoaxaca.backend.entity.enums.MetodoPago;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.DonacionMapper;
import com.huellitasoaxaca.backend.repository.DonacionRepository;
import com.huellitasoaxaca.backend.services.DonacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DonacionServiceImpl implements DonacionService
{
    private final DonacionRepository donacionRepository;
    private final DonacionMapper donacionMapper;

    @Override
    public List<DonacionResponse> listarTodas() {
        return convertirLista(donacionRepository.findAll());
    }

    @Override
    public DonacionResponse obtenerPorId(Long id) {
        Donacion donacion = donacionRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la donación con ID " + id
                        )
                );

        return donacionMapper.toResponse(donacion);
    }

    @Override
    public List<DonacionResponse> listarPorUsuario(Long usuarioId) {
        return convertirLista(
                donacionRepository.findByUsuarioId(usuarioId)
        );
    }

    @Override
    public List<DonacionResponse> listarPorRefugio(Long refugioId) {
        return convertirLista(
                donacionRepository.findByRefugioId(refugioId)
        );
    }

    @Override
    public List<DonacionResponse> listarPorEstatus(
            EstatusDonacion estatus
    ) {
        return convertirLista(
                donacionRepository.findByEstatus(estatus)
        );
    }

    @Override
    public List<DonacionResponse> listarPorMetodoPago(
            MetodoPago metodoPago
    ) {
        return convertirLista(
                donacionRepository.findByMetodoPago(metodoPago)
        );
    }

    @Override
    public List<DonacionResponse> listarPorRangoFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        return convertirLista(
                donacionRepository.findByFechaBetween(
                        fechaInicio,
                        fechaFin
                )
        );
    }

    private List<DonacionResponse> convertirLista(
            List<Donacion> donaciones
    ) {
        return donaciones.stream()
                .map(donacionMapper::toResponse)
                .toList();
    }
}
