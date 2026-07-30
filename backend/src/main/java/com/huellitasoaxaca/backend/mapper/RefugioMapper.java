package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.RefugioPerfilResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminResumenResponse;
import com.huellitasoaxaca.backend.dto.response.ResponsableRefugioResumenResponse;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;

@Component
public class RefugioMapper 
{
    public RefugioResponse toResponse(Refugio refugio) 
    {
        if (refugio == null) 
        {
            return null;
        }

        Usuario responsable = refugio.getUsuario();

        return new RefugioResponse(
                refugio.getId(),
                refugio.getNombre(),
                refugio.getDescripcion(),
                refugio.getDireccion(),
                refugio.getTelefono(),
                refugio.getCorreo(),
                refugio.getActivo(),
                responsable != null ? responsable.getId() : null,
                obtenerNombreResponsable(responsable)
        );
    }

    public RefugioPerfilResponse toPerfilResponse(Refugio refugio)
    {
        return new RefugioPerfilResponse(
                refugio.getId(),
                refugio.getNombre(),
                refugio.getDescripcion(),
                refugio.getDireccion(),
                refugio.getTelefono(),
                refugio.getCorreo(),
                refugio.getActivo()
        );
    }

    public RefugioAdminResumenResponse toAdminResumen(Refugio refugio)
    {
        return new RefugioAdminResumenResponse(
                refugio.getId(),
                refugio.getNombre(),
                refugio.getCorreo(),
                refugio.getAprobado(),
                refugio.getActivo(),
                toResponsableResumen(refugio.getUsuario())
        );
    }

    public RefugioAdminDetalleResponse toAdminDetalle(Refugio refugio)
    {
        Usuario aprobadoPor = refugio.getAprobadoPor();
        return new RefugioAdminDetalleResponse(
                refugio.getId(),
                refugio.getNombre(),
                refugio.getDescripcion(),
                refugio.getDireccion(),
                refugio.getTelefono(),
                refugio.getCorreo(),
                refugio.getAprobado(),
                refugio.getActivo(),
                refugio.getFechaAprobacion(),
                toResponsableResumen(refugio.getUsuario()),
                aprobadoPor != null ? aprobadoPor.getId() : null
        );
    }

    private ResponsableRefugioResumenResponse toResponsableResumen(
            Usuario usuario
    )
    {
        if (usuario == null)
        {
            return null;
        }
        return new ResponsableRefugioResumenResponse(
                usuario.getId(),
                obtenerNombreResponsable(usuario),
                usuario.getActivo(),
                usuario.getRol().getNombre()
        );
    }

    private String obtenerNombreResponsable(Usuario usuario) 
    {
        if (usuario == null) 
        {
            return null;
        }

        return String.join(
                " ",
                usuario.getNombre(),
                usuario.getApellidoPaterno()
        );
    }
}
