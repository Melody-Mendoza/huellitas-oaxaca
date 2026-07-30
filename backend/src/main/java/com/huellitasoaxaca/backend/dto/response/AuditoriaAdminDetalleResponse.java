package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record AuditoriaAdminDetalleResponse(
        Long id,
        String tipoAccion,
        String tipoRecurso,
        Long recursoId,
        String motivo,
        Map<String, Object> estadoAnterior,
        Map<String, Object> estadoNuevo,
        String resultado,
        LocalDateTime fecha,
        Map<String, Object> metadatos,
        Long administradorId,
        String nombreAdministrador
)
{}
