package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.HistorialEstadoResponse;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

import java.util.List;

public interface HistorialEstadoService 
{
    List<HistorialEstadoResponse> listarPorSolicitud(Long solicitudId);

    List<HistorialEstadoResponse> listarPorEstado(
            EstadoSolicitud estado
    );

    HistorialEstadoResponse obtenerPorId(Long id);
}
