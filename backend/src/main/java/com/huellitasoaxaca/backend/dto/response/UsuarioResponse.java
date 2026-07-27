package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

public record UsuarioResponse(
    Long id,
    String nombre,
    String apellidoPaterno,
    String apellidoMaterno,
    String correo,
    String telefono,
    String fotoPerfil,
    Boolean activo,
    LocalDateTime fechaRegistro,
    RolResponse rol
) 
{}
