package com.huellitasoaxaca.backend.services;

import java.util.Map;

import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.TipoAccionAuditoria;

public interface AuditoriaAdministrativaService
{
    void registrarCambioEstadoUsuario(
            Usuario administrador,
            Usuario usuario,
            boolean estadoAnterior,
            boolean estadoNuevo,
            String motivo
    );

    void registrarAccionRefugio(
            Usuario administrador,
            Refugio refugio,
            TipoAccionAuditoria accion,
            String motivo,
            Map<String, Object> estadoAnterior,
            Map<String, Object> estadoNuevo,
            Map<String, Object> metadatos
    );
}
