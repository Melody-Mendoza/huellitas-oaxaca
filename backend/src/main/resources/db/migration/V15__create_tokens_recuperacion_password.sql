CREATE TABLE tokens_recuperacion_password
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NOT NULL,
    utilizado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_token_recuperacion_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);

CREATE INDEX idx_token_recuperacion_token
    ON tokens_recuperacion_password(token);

CREATE INDEX idx_token_recuperacion_usuario
    ON tokens_recuperacion_password(usuario_id);

CREATE INDEX idx_token_recuperacion_expiracion
    ON tokens_recuperacion_password(fecha_expiracion);