package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdminResumenResponse;

public interface AdminSolicitudService
{
    PaginaResponse<SolicitudAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String texto,
            String estado,
            Long mascotaId,
            Long refugioId,
            String correoAdministrador
    );

    SolicitudAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    );
}
