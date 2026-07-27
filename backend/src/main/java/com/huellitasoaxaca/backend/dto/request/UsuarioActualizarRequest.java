package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.Email;
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

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @Pattern(
                regexp = "^$|^[0-9]{10}$",
                message = "El teléfono debe contener 10 dígitos"
        )
        String telefono,

        @Size(max = 255, message = "La URL de la imagen es demasiado larga")
        String fotoPerfil
) 
{}
