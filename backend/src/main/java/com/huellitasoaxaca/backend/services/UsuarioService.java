package com.huellitasoaxaca.backend.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.huellitasoaxaca.backend.dto.request.CambiarPasswordRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioActualizarRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioAdminCrearRequest;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;

public interface UsuarioService 
{
        List<UsuarioResponse> listarTodos();

        PaginaResponse<UsuarioResponse> listarAdministrativamente(
                int page,
                int size,
                String sort,
                String texto,
                String rol,
                String activo,
                String correoAdministrador
        );

        UsuarioResponse obtenerAdministrativamente(
                Long id,
                String correoAdministrador
        );

        UsuarioResponse obtenerPorId(Long id);

        UsuarioResponse obtenerActivoPorCorreo(String correo);

        List<UsuarioResponse> listarActivos();

        UsuarioResponse actualizarPerfil(
                String correoAutenticado,
                UsuarioActualizarRequest request
        );

        UsuarioResponse actualizarFotoPerfil(
                String correoAutenticado,
                MultipartFile foto
        );

        UsuarioResponse eliminarFotoPerfil(
                String correoAutenticado
        );

        void cambiarPassword(
                String correoAutenticado,
                CambiarPasswordRequest request
        );

        UsuarioResponse cambiarEstado(
                Long id,
                Boolean activo,
                String motivo,
                String correoAdministrador
        );

        UsuarioResponse crearAdministrador(
                UsuarioAdminCrearRequest request,
                String correoAdministrador
        );
}
