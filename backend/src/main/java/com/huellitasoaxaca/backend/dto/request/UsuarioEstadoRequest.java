package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record UsuarioEstadoRequest(
    @NotNull(message = "El estado activo es obligatorio")
    Boolean activo
) 
{}
