CREATE TABLE mascotas 
(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    especie ENUM('PERRO','GATO') NOT NULL,

    raza VARCHAR(100),

    sexo ENUM('MACHO','HEMBRA'),

    edad INT,

    peso DECIMAL(5,2),

    tamano ENUM('PEQUENO','MEDIANO','GRANDE'),

    color VARCHAR(80),

    descripcion TEXT,

    estado ENUM('DISPONIBLE','ADOPTADO','EN_PROCESO'),

    fecha_ingreso DATE,

    imagen VARCHAR(255),

    refugio_id BIGINT,

    CONSTRAINT fk_mascota_refugio
        FOREIGN KEY(refugio_id)
        REFERENCES refugios(id)
);