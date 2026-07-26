CREATE TABLE imagenes_mascotas 
(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    url VARCHAR(255),

    mascota_id BIGINT,

    CONSTRAINT fk_imagen_mascota
        FOREIGN KEY(mascota_id)
        REFERENCES mascotas(id)
);