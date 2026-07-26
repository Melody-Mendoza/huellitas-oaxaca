CREATE TABLE favoritos 
(

    usuario_id BIGINT,

    mascota_id BIGINT,

    PRIMARY KEY(usuario_id, mascota_id),

    CONSTRAINT fk_favorito_usuario
        FOREIGN KEY(usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_favorito_mascota
        FOREIGN KEY(mascota_id)
        REFERENCES mascotas(id)
);