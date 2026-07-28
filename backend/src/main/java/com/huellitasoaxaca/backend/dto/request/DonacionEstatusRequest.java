package com.huellitasoaxaca.backend.dto.request;

import com.huellitasoaxaca.backend.entity.enums.EstatusDonacion;

import jakarta.validation.constraints.NotNull;

public record DonacionEstatusRequest(
    @NotNull(message = "El estatus es obligatorio")
    EstatusDonacion estatus

) 
{}
