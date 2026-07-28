CREATE TABLE tokens_revocados
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    jti VARCHAR(100) NOT NULL UNIQUE,
    correo_usuario VARCHAR(150) NOT NULL,
    fecha_revocacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NOT NULL
);

CREATE INDEX idx_token_revocado_jti
    ON tokens_revocados(jti);

CREATE INDEX idx_token_revocado_expiracion
    ON tokens_revocados(fecha_expiracion);