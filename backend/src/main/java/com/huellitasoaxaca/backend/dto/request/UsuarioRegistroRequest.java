package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record UsuarioRegistroRequest(
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

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(
                min = 8,
                max = 14,
                message = "La contraseña debe tener entre 8 y 14 caracteres"
        )
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La contraseña debe contener una mayúscula, un número y un carácter especial"
        )
        String password,

        @Pattern(
                regexp = "^$|^[0-9]{10}$",
                message = "El teléfono debe contener 10 dígitos"
        )
        String telefono
) 
{}
