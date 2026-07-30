ALTER TABLE donaciones
    ADD COLUMN clave_idempotencia VARCHAR(64)
        CHARACTER SET ascii
        COLLATE ascii_bin
        NULL
        AFTER refugio_id,
    ADD COLUMN fecha_actualizacion TIMESTAMP(6)
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP(6)
        AFTER fecha;

UPDATE donaciones
SET clave_idempotencia = CONCAT('LEGACY_', id)
WHERE clave_idempotencia IS NULL;

ALTER TABLE donaciones
    MODIFY COLUMN clave_idempotencia VARCHAR(64)
        CHARACTER SET ascii
        COLLATE ascii_bin
        NOT NULL,
    ADD CONSTRAINT uq_donaciones_usuario_idempotencia
        UNIQUE (usuario_id, clave_idempotencia),
    ADD CONSTRAINT chk_donaciones_monto_simulado
        CHECK (monto >= 10.00 AND monto <= 50000.00);

CREATE INDEX idx_donaciones_usuario_fecha
    ON donaciones (usuario_id, fecha DESC, id DESC);

CREATE INDEX idx_donaciones_refugio_fecha
    ON donaciones (refugio_id, fecha DESC, id DESC);
