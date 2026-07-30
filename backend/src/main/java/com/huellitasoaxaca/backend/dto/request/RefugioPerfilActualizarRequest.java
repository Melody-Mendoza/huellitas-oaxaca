package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RefugioPerfilActualizarRequest(
        @Size(
                max = 150,
                message = "El nombre no puede superar los 150 caracteres"
        )
        @Pattern(
                regexp = ".*\\S.*",
                message = "El nombre del refugio no puede estar vacío"
        )
        String nombre,

        @Size(
                max = 1000,
                message = "La descripción no puede superar los 1000 caracteres"
        )
        String descripcion,

        @Size(
                max = 255,
                message = "La dirección no puede superar los 255 caracteres"
        )
        String direccion,

        @Pattern(
                regexp = "^$|^[0-9]{10}$",
                message = "El teléfono debe contener 10 dígitos"
        )
        String telefono,

        @Email(message = "El correo no tiene un formato válido")
        @Size(
                max = 150,
                message = "El correo no puede superar los 150 caracteres"
        )
        String correo
) {}
