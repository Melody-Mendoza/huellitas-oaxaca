package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminResumenResponse;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;

public interface AdminAuditoriaService
{
    PaginaResponse<AuditoriaAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String tipoAccion,
            String tipoRecurso,
            Long administradorId,
            String desde,
            String hasta,
            String correoAdministrador
    );

    AuditoriaAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    );
}
