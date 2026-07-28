package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FavoritoCrearRequest(
    @NotNull(message = "La mascota es obligatoria")
    @Positive(message = "El identificador de la mascota debe ser válido")
    Long mascotaId) 
{}
