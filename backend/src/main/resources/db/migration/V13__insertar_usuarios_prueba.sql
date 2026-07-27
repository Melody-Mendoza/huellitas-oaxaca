INSERT INTO usuarios (
    nombre,
    apellido_paterno,
    apellido_materno,
    correo,
    password,
    telefono,
    foto_perfil,
    activo,
    fecha_registro,
    rol_id
)
SELECT
    'Administrador',
    'Huellitas',
    'Oaxaca',
    'admin@huellitasoaxaca.com',
    '$2a$10$dPVe8.baWzFDCFQHOUoCbulzZcjjMi.WJ5vSHaNYOlQYW.kcL4s7y',
    '9510000001',
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    r.id
FROM roles r
WHERE r.nombre = 'ADMIN'
AND NOT EXISTS (
    SELECT 1
    FROM usuarios
    WHERE correo = 'admin@huellitasoaxaca.com'
);

INSERT INTO usuarios (
    nombre,
    apellido_paterno,
    apellido_materno,
    correo,
    password,
    telefono,
    foto_perfil,
    activo,
    fecha_registro,
    rol_id
)
SELECT
    'Responsable',
    'Refugio',
    'Oaxaca',
    'refugio@huellitasoaxaca.com',
    '$2a$10$B3vQ3Br2kXtrRGMcp0jdeuj2rDcd8cdtnJQFW2BZK2hK8Yrb7lSY6',
    '9510000002',
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    r.id
FROM roles r
WHERE r.nombre = 'REFUGIO'
AND NOT EXISTS (
    SELECT 1
    FROM usuarios
    WHERE correo = 'refugio@huellitasoaxaca.com'
);