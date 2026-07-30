package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RefugioAdminCrearRequest(
        @NotBlank(message = "El nombre del refugio es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String descripcion,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
        String direccion,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "El teléfono debe contener 10 dígitos"
        )
        String telefono,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @NotNull(message = "El responsable es obligatorio")
        Long responsableId,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
        String motivo
)
{}
