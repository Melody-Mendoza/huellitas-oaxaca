package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecuperarPasswordRequest(
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(
        max = 150,
        message = "El correo no puede superar los 150 caracteres"
    )
    String correo
) 
{}
