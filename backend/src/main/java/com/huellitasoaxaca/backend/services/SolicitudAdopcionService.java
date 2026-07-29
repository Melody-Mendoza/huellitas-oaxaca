package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.request.SolicitudAdopcionCrearRequest;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdopcionResponse;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

import java.util.List;

public interface SolicitudAdopcionService 
{
    SolicitudAdopcionResponse crear(
            SolicitudAdopcionCrearRequest request,
            String correoAutenticado
    );

    List<SolicitudAdopcionResponse> listarTodas();

    SolicitudAdopcionResponse obtenerPorId(Long id);

    List<SolicitudAdopcionResponse> listarPorUsuario(Long usuarioId);

    List<SolicitudAdopcionResponse> listarPorMascota(Long mascotaId);

    List<SolicitudAdopcionResponse> listarPorEstado(
            EstadoSolicitud estado
    );

    List<SolicitudAdopcionResponse> listarPorUsuarioYEstado(
            Long usuarioId,
            EstadoSolicitud estado
    );
}
