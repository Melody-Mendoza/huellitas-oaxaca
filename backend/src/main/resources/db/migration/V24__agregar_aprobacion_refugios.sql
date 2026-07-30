ALTER TABLE refugios
    ADD COLUMN aprobado BOOLEAN NULL AFTER activo,
    ADD COLUMN fecha_aprobacion TIMESTAMP(6) NULL AFTER aprobado,
    ADD COLUMN aprobado_por BIGINT NULL AFTER fecha_aprobacion;

UPDATE refugios
SET aprobado = TRUE,
    fecha_aprobacion = CURRENT_TIMESTAMP(6);

ALTER TABLE refugios
    MODIFY COLUMN aprobado BOOLEAN NOT NULL DEFAULT FALSE,
    ADD CONSTRAINT fk_refugio_aprobado_por
        FOREIGN KEY (aprobado_por)
        REFERENCES usuarios(id),
    ADD CONSTRAINT chk_refugio_activo_aprobado
        CHECK (activo IS NULL OR activo = FALSE OR aprobado = TRUE);

CREATE INDEX idx_refugios_aprobado_activo
    ON refugios (aprobado, activo);

CREATE INDEX idx_refugios_responsable_estado
    ON refugios (usuario_id, aprobado, activo);
