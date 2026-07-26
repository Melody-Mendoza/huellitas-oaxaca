CREATE TABLE solicitudes_adopcion 
(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    estado ENUM(
        'PENDIENTE',
        'APROBADA',
        'RECHAZADA'
    ) DEFAULT 'PENDIENTE',

    comentarios TEXT,

    usuario_id BIGINT,

    mascota_id BIGINT,

    CONSTRAINT fk_solicitud_usuario
        FOREIGN KEY(usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_solicitud_mascota
        FOREIGN KEY(mascota_id)
        REFERENCES mascotas(id)
);