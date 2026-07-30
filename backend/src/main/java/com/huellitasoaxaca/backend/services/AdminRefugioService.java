package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.request.RefugioAdminCrearRequest;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminResumenResponse;

public interface AdminRefugioService
{
    PaginaResponse<RefugioAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String texto,
            String aprobado,
            String activo,
            Long responsableId,
            String correoAdministrador
    );

    RefugioAdminDetalleResponse crear(
            RefugioAdminCrearRequest request,
            String correoAdministrador
    );

    RefugioAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    );

    RefugioAdminDetalleResponse cambiarResponsable(
            Long id,
            Long responsableId,
            String motivo,
            String correoAdministrador
    );

    RefugioAdminDetalleResponse cambiarAprobacion(
            Long id,
            Boolean aprobado,
            String motivo,
            String correoAdministrador
    );

    RefugioAdminDetalleResponse cambiarEstado(
            Long id,
            Boolean activo,
            String motivo,
            String correoAdministrador
    );
}
