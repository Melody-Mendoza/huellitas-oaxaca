package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.entity.Usuario;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioMapper 
{
    private final RolMapper rolMapper;

    public UsuarioResponse toResponse(Usuario usuario) 
    {
        if (usuario == null) 
        {
            return null;
        }

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidoPaterno(),
                usuario.getApellidoMaterno(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getFotoPerfil(),
                usuario.getActivo(),
                usuario.getFechaRegistro(),
                rolMapper.toResponse(usuario.getRol())
        );
    }
}
