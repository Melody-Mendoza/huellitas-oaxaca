package com.huellitasoaxaca.backend.services;

import java.util.List;

import com.huellitasoaxaca.backend.dto.response.RefugioDonacionResponse;

public interface RefugioDonacionService
{
    List<RefugioDonacionResponse> listarDisponibles();
}
