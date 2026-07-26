CREATE TABLE historial_estado 
(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    solicitud_id BIGINT,

    estado VARCHAR(40),

    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    observaciones TEXT,

    CONSTRAINT fk_historial_solicitud
        FOREIGN KEY(solicitud_id)
        REFERENCES solicitudes_adopcion(id)
);