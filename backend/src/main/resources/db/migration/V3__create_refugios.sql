CREATE TABLE refugios 
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(150),
    sitio_web VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,

    usuario_id BIGINT,

    CONSTRAINT fk_refugio_usuario
        FOREIGN KEY(usuario_id)
        REFERENCES usuarios(id)
);