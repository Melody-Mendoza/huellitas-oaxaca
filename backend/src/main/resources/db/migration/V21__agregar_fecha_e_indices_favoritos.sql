ALTER TABLE favoritos
    ADD COLUMN fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_favoritos_usuario_fecha
    ON favoritos (usuario_id, fecha_agregado DESC, mascota_id DESC);

CREATE INDEX idx_favoritos_mascota
    ON favoritos (mascota_id);
