package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.MascotaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaAdminResumenResponse;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;

public interface AdminMascotaService
{
    PaginaResponse<MascotaAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String texto,
            String especie,
            String sexo,
            String tamano,
            String estado,
            Long refugioId,
            String correoAdministrador
    );

    MascotaAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    );
}
