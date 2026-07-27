package com.huellitasoaxaca.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import com.huellitasoaxaca.backend.dto.response.DonacionResponse;
import com.huellitasoaxaca.backend.entity.enums.EstatusDonacion;
import com.huellitasoaxaca.backend.entity.enums.MetodoPago;

public interface DonacionService 
{
    List<DonacionResponse> listarTodas();

    DonacionResponse obtenerPorId(Long id);

    List<DonacionResponse> listarPorUsuario(Long usuarioId);

    List<DonacionResponse> listarPorRefugio(Long refugioId);

    List<DonacionResponse> listarPorEstatus(EstatusDonacion estatus);

    List<DonacionResponse> listarPorMetodoPago(MetodoPago metodoPago);

    List<DonacionResponse> listarPorRangoFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}
