package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminResumenResponse;
import com.huellitasoaxaca.backend.entity.AuditoriaAdministrativa;
import com.huellitasoaxaca.backend.entity.Usuario;

@Component
public class AdminAuditoriaMapper
{
    public AuditoriaAdminResumenResponse toAdminResumen(AuditoriaAdministrativa auditoria)
    {
        Usuario admin = auditoria.getAdministrador();
        return new AuditoriaAdminResumenResponse(
                auditoria.getId(),
                auditoria.getTipoAccion().name(),
                auditoria.getTipoRecurso().name(),
                auditoria.getRecursoId(),
                auditoria.getResultado().name(),
                auditoria.getFecha(),
                admin != null ? admin.getId() : null,
                admin != null
                        ? admin.getNombre() + " " + admin.getApellidoPaterno()
                        : null
        );
    }

    public AuditoriaAdminDetalleResponse toAdminDetalle(AuditoriaAdministrativa auditoria)
    {
        Usuario admin = auditoria.getAdministrador();
        return new AuditoriaAdminDetalleResponse(
                auditoria.getId(),
                auditoria.getTipoAccion().name(),
                auditoria.getTipoRecurso().name(),
                auditoria.getRecursoId(),
                auditoria.getMotivo(),
                auditoria.getEstadoAnterior(),
                auditoria.getEstadoNuevo(),
                auditoria.getResultado().name(),
                auditoria.getFecha(),
                auditoria.getMetadatos(),
                admin != null ? admin.getId() : null,
                admin != null
                        ? admin.getNombre() + " " + admin.getApellidoPaterno()
                        : null
        );
    }
}
