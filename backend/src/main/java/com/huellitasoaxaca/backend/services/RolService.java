package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.RolResponse;
import java.util.List;

public interface RolService 
{
    List<RolResponse> listarTodos();

    RolResponse obtenerPorId(Long id);

    RolResponse obtenerPorNombre(String nombre);
}
