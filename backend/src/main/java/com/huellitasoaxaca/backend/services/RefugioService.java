package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.RefugioResponse;
import java.util.List;

public interface RefugioService 
{
    List<RefugioResponse> listarTodos();

    List<RefugioResponse> listarActivos();

    RefugioResponse obtenerPorId(Long id);

    RefugioResponse obtenerPorNombre(String nombre);

    List<RefugioResponse> listarPorUsuario(Long usuarioId);

    void desactivar(Long id);    
}
