package com.huellitasoaxaca.backend.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huellitasoaxaca.backend.entity.AuditoriaAdministrativa;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.ResultadoAuditoria;
import com.huellitasoaxaca.backend.entity.enums.TipoAccionAuditoria;
import com.huellitasoaxaca.backend.entity.enums.TipoRecursoAuditoria;
import com.huellitasoaxaca.backend.repository.AuditoriaAdministrativaRepository;
import com.huellitasoaxaca.backend.services.AuditoriaAdministrativaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditoriaAdministrativaServiceImpl
        implements AuditoriaAdministrativaService
{
    private final AuditoriaAdministrativaRepository auditoriaRepository;

    @Override
    public void registrarCambioEstadoUsuario(
            Usuario administrador,
            Usuario usuario,
            boolean estadoAnterior,
            boolean estadoNuevo,
            String motivo
    )
    {
        auditoriaRepository.save(AuditoriaAdministrativa.builder()
                .administrador(administrador)
                .tipoAccion(estadoNuevo
                        ? TipoAccionAuditoria.ACTIVAR_USUARIO
                        : TipoAccionAuditoria.DESACTIVAR_USUARIO)
                .tipoRecurso(TipoRecursoAuditoria.USUARIO)
                .recursoId(usuario.getId())
                .motivo(motivo)
                .estadoAnterior(Map.of("activo", estadoAnterior))
                .estadoNuevo(Map.of("activo", estadoNuevo))
                .resultado(ResultadoAuditoria.EXITOSA)
                .fecha(LocalDateTime.now())
                .metadatos(Map.of(
                        "rol", usuario.getRol().getNombre()
                ))
                .build());
    }

    @Override
    public void registrarAccionRefugio(
            Usuario administrador,
            Refugio refugio,
            TipoAccionAuditoria accion,
            String motivo,
            Map<String, Object> estadoAnterior,
            Map<String, Object> estadoNuevo,
            Map<String, Object> metadatos
    )
    {
        auditoriaRepository.save(AuditoriaAdministrativa.builder()
                .administrador(administrador)
                .tipoAccion(accion)
                .tipoRecurso(TipoRecursoAuditoria.REFUGIO)
                .recursoId(refugio.getId())
                .motivo(motivo)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .resultado(ResultadoAuditoria.EXITOSA)
                .fecha(LocalDateTime.now())
                .metadatos(metadatos)
                .build());
    }
}
