package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record UsuarioActualizarRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 30, message = "El nombre no puede superar los 30 caracteres")
        String nombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(
                max = 20,
                message = "El apellido paterno no puede superar los 20 caracteres"
        )
        String apellidoPaterno,

        @Size(
                max = 20,
                message = "El apellido materno no puede superar los 20 caracteres"
        )
        String apellidoMaterno,

        @Pattern(
                regexp = "^$|^[0-9]{10}$",
                message = "El teléfono debe contener 10 dígitos"
        )
        String telefono
) 
{}
