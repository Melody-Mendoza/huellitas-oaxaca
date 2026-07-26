CREATE TABLE donaciones 
(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    monto DECIMAL(10,2),

    metodo_pago ENUM(
        'EFECTIVO',
        'TRANSFERENCIA',
        'PAYPAL'
    ),

    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    estatus ENUM(
        'PENDIENTE',
        'COMPLETADA',
        'CANCELADA'
    ),

    mensaje TEXT,

    usuario_id BIGINT,

    refugio_id BIGINT,

    CONSTRAINT fk_donacion_usuario
        FOREIGN KEY(usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_donacion_refugio
        FOREIGN KEY(refugio_id)
        REFERENCES refugios(id)
);