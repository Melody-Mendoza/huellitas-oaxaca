CREATE TABLE auditoria_administrativa
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    administrador_id BIGINT NOT NULL,
    tipo_accion VARCHAR(64) NOT NULL,
    tipo_recurso VARCHAR(40) NOT NULL,
    recurso_id BIGINT NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    estado_anterior JSON,
    estado_nuevo JSON,
    resultado VARCHAR(20) NOT NULL,
    fecha TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    metadatos JSON,

    CONSTRAINT fk_auditoria_administrador
        FOREIGN KEY (administrador_id)
        REFERENCES usuarios(id)
);

CREATE INDEX idx_auditoria_fecha
    ON auditoria_administrativa (fecha);

CREATE INDEX idx_auditoria_administrador_fecha
    ON auditoria_administrativa (administrador_id, fecha);

CREATE INDEX idx_auditoria_recurso
    ON auditoria_administrativa
        (tipo_recurso, recurso_id, fecha);

CREATE INDEX idx_auditoria_accion_fecha
    ON auditoria_administrativa (tipo_accion, fecha);
