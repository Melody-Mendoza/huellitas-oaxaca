package com.huellitasoaxaca.backend.services;

import java.util.List;

import com.huellitasoaxaca.backend.dto.request.CambiarPasswordRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;

public interface UsuarioService 
{
        List<UsuarioResponse> listarTodos();

        UsuarioResponse obtenerPorId(Long id);

        UsuarioResponse obtenerPorCorreo(String correo);

        List<UsuarioResponse> listarActivos();

        UsuarioResponse actualizarPerfil(
                String correoAutenticado,
                UsuarioActualizarRequest request
        );

        void cambiarPassword(
                String correoAutenticado,
                CambiarPasswordRequest request
        );

        UsuarioResponse cambiarEstado(
                Long id,
                Boolean activo
        );

        void desactivar(Long id);
}
