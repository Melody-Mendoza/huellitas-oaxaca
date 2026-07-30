package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.request.RefugioPerfilActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.RefugioPanelResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioPerfilResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioResponse;
import java.util.List;

public interface RefugioService 
{
    List<RefugioPerfilResponse> listarPerfilesPropios(
            String correoAutenticado
    );

    RefugioPerfilResponse obtenerPerfilPropio(
            Long refugioId,
            String correoAutenticado
    );

    RefugioPerfilResponse actualizarPerfilPropio(
            Long refugioId,
            RefugioPerfilActualizarRequest request,
            String correoAutenticado
    );

    RefugioPanelResponse obtenerPanelPropio(
            Long refugioId,
            String correoAutenticado
    );

    List<RefugioResponse> listarTodos();

    List<RefugioResponse> listarActivos();

    RefugioResponse obtenerPorId(Long id);

    RefugioResponse obtenerPorNombre(String nombre);

    List<RefugioResponse> listarPorUsuario(Long usuarioId);

    void desactivar(Long id);    
}
