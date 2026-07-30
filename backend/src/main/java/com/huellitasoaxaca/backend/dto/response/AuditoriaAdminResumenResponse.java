package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

public record AuditoriaAdminResumenResponse(
        Long id,
        String tipoAccion,
        String tipoRecurso,
        Long recursoId,
        String resultado,
        LocalDateTime fecha,
        Long administradorId,
        String nombreAdministrador
)
{}
