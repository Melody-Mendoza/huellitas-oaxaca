package com.huellitasoaxaca.backend.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.huellitasoaxaca.backend.entity.enums.ResultadoAuditoria;
import com.huellitasoaxaca.backend.entity.enums.TipoAccionAuditoria;
import com.huellitasoaxaca.backend.entity.enums.TipoRecursoAuditoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auditoria_administrativa")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditoriaAdministrativa
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "administrador_id", nullable = false)
    private Usuario administrador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false, length = 64)
    private TipoAccionAuditoria tipoAccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso", nullable = false, length = 40)
    private TipoRecursoAuditoria tipoRecurso;

    @Column(name = "recurso_id", nullable = false)
    private Long recursoId;

    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "estado_anterior", columnDefinition = "json")
    private Map<String, Object> estadoAnterior;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "estado_nuevo", columnDefinition = "json")
    private Map<String, Object> estadoNuevo;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 20)
    private ResultadoAuditoria resultado;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadatos", columnDefinition = "json")
    private Map<String, Object> metadatos;
}
