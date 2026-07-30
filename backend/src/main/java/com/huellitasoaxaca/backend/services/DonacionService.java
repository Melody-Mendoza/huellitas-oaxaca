package com.huellitasoaxaca.backend.services;

import org.springframework.data.domain.Page;

import com.huellitasoaxaca.backend.dto.request.DonacionCrearRequest;
import com.huellitasoaxaca.backend.dto.response.DonacionResponse;

public interface DonacionService 
{
    ResultadoCreacion crear(
            DonacionCrearRequest request,
            String claveIdempotencia,
            String correoAutenticado
    );

    Page<DonacionResponse> listarPropias(
            String correoAutenticado,
            int page,
            int size
    );

    DonacionResponse obtenerPropia(Long donacionId, String correoAutenticado);

    DonacionResponse confirmar(Long donacionId, String correoAutenticado);

    DonacionResponse cancelar(Long donacionId, String correoAutenticado);

    record ResultadoCreacion(DonacionResponse donacion, boolean nueva) {}
}
