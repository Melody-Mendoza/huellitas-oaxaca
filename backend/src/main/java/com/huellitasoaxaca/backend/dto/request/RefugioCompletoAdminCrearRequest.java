package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RefugioCompletoAdminCrearRequest(
        @NotBlank(message = "El nombre del responsable es obligatorio")
        @Size(max = 30, message = "El nombre no puede superar los 30 caracteres")
        String responsableNombre,

        @NotBlank(message = "El apellido paterno del responsable es obligatorio")
        @Size(max = 20, message = "El apellido paterno no puede superar los 20 caracteres")
        String responsableApellidoPaterno,

        @Size(max = 20, message = "El apellido materno no puede superar los 20 caracteres")
        String responsableApellidoMaterno,

        @NotBlank(message = "El correo del responsable es obligatorio")
        @Email(message = "El correo del responsable no tiene un formato válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String responsableCorreo,

        @NotBlank(message = "El teléfono del responsable es obligatorio")
        @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe contener 10 dígitos")
        String responsableTelefono,

        @NotBlank(message = "La contraseña del responsable es obligatoria")
        @Size(min = 8, max = 14, message = "La contraseña debe tener entre 8 y 14 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$", message = "La contraseña debe contener una mayúscula, un número y un carácter especial")
        String responsablePassword,

        @NotBlank(message = "El nombre del refugio es obligatorio")
        @Size(max = 150, message = "El nombre del refugio no puede superar los 150 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String descripcion,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
        String direccion,

        @NotBlank(message = "El teléfono del refugio es obligatorio")
        @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe contener 10 dígitos")
        String telefono,

        @NotBlank(message = "El correo del refugio es obligatorio")
        @Email(message = "El correo del refugio no tiene un formato válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
        String motivo
)
{}
