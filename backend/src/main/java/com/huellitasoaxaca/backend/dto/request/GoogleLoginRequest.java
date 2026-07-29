package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "El Firebase ID token es obligatorio")
        String idToken
)
{}
