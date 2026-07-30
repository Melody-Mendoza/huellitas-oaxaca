package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

public record RefugioAdminDetalleResponse(
        Long id,
        String nombre,
        String descripcion,
        String direccion,
        String telefono,
        String correo,
        Boolean aprobado,
        Boolean activo,
        LocalDateTime fechaAprobacion,
        ResponsableRefugioResumenResponse responsable,
        Long aprobadoPorId
)
{}
