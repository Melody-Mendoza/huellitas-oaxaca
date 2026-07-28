package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RestablecerPasswordRequest(
    @NotBlank(message = "El token es obligatorio")
    String token,

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(
        min = 8,
        max = 14,
        message = "La contraseña debe tener entre 8 y 14 caracteres"
    )
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
        message = "La contraseña debe contener una mayúscula, un número y un carácter especial"
    )
    String nuevaPassword,

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    String confirmarPassword
) 
{}
