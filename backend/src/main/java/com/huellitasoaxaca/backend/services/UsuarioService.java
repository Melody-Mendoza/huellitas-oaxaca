package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import java.util.List;

public interface UsuarioService 
{
    List<UsuarioResponse> listarTodos();

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse obtenerPorCorreo(String correo);

    List<UsuarioResponse> listarActivos();

    void desactivar(Long id);
}
