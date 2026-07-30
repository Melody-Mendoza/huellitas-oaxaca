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
    NULL,
    'admin@huellitasoaxaca.mx',
    '$2a$10$E975AIOn2tVVZSOgMufqI.Vfnznpiegng/ieLGZ.mCGoklfgRLd8G',
    '9510000001',
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    r.id
FROM roles r
WHERE r.nombre = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u WHERE u.correo = 'admin@huellitasoaxaca.mx'
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
    'Refugio',
    'Oaxaca',
    NULL,
    'refugio@huellitasoaxaca.mx',
    '$2a$10$DMFYcZCEoksqiT6ZwA4/k.uZntfIujXEW4CgZtLDfkujJX4StH5zK',
    '9511234567',
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    r.id
FROM roles r
WHERE r.nombre = 'REFUGIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u WHERE u.correo = 'refugio@huellitasoaxaca.mx'
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
    'Usuario',
    'Demostración',
    NULL,
    'usuario@huellitasoaxaca.mx',
    '$2a$10$GFP1Kss05SkPE5mokv/PtufRZl.ZzpBHpT4rG80WvkYiIn./KFA9O',
    '9510000003',
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    r.id
FROM roles r
WHERE r.nombre = 'USUARIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u WHERE u.correo = 'usuario@huellitasoaxaca.mx'
  );

-- 2) Refugio de demostración (aprobado y activo para catálogo, panel y donaciones)
INSERT INTO refugios (
    nombre,
    descripcion,
    direccion,
    telefono,
    correo,
    activo,
    aprobado,
    fecha_aprobacion,
    aprobado_por,
    usuario_id
)
SELECT
    'Refugio Patitas de Oaxaca',
    'Refugio dedicado al rescate, cuidado y adopción responsable de perros y gatos en Oaxaca.',
    'Oaxaca de Juárez, Oaxaca',
    '9511234567',
    'refugio@huellitasoaxaca.mx',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP(6),
    admin_user.id,
    refugio_user.id
FROM usuarios refugio_user
CROSS JOIN usuarios admin_user
WHERE refugio_user.correo = 'refugio@huellitasoaxaca.mx'
  AND admin_user.correo = 'admin@huellitasoaxaca.mx'
  AND NOT EXISTS (
      SELECT 1
      FROM refugios rf
      WHERE rf.usuario_id = refugio_user.id
        AND rf.nombre = 'Refugio Patitas de Oaxaca'
  );

-- 3) Mascotas disponibles
INSERT INTO mascotas (
    nombre,
    especie,
    raza,
    sexo,
    edad,
    peso,
    tamano,
    descripcion,
    estado,
    fecha_ingreso,
    imagen,
    refugio_id
)
SELECT
    'Luna',
    'PERRO',
    'Mestiza',
    'HEMBRA',
    2,
    12.50,
    'MEDIANO',
    'Luna es juguetona y sociable. Convive bien con otras mascotas, tiene mucha energía y busca un hogar con patio o paseos diarios. Está sana, vacunada y desparasitada.',
    'DISPONIBLE',
    DATE_SUB(CURDATE(), INTERVAL 45 DAY),
    '/media/mascotas/demo-luna.jpg',
    rf.id
FROM refugios rf
INNER JOIN usuarios u ON u.id = rf.usuario_id
WHERE u.correo = 'refugio@huellitasoaxaca.mx'
  AND rf.nombre = 'Refugio Patitas de Oaxaca'
  AND NOT EXISTS (
      SELECT 1 FROM mascotas m WHERE m.refugio_id = rf.id AND m.nombre = 'Luna'
  );

INSERT INTO mascotas (
    nombre,
    especie,
    raza,
    sexo,
    edad,
    peso,
    tamano,
    descripcion,
    estado,
    fecha_ingreso,
    imagen,
    refugio_id
)
SELECT
    'Max',
    'PERRO',
    'Labrador mestizo',
    'MACHO',
    4,
    28.00,
    'GRANDE',
    'Max es leal y tranquilo con la familia. Ideal para un hogar con espacio y rutinas claras. Disfruta la compañía humana y se lleva bien con perros calmados.',
    'DISPONIBLE',
    DATE_SUB(CURDATE(), INTERVAL 60 DAY),
    '/media/mascotas/demo-max.jpg',
    rf.id
FROM refugios rf
INNER JOIN usuarios u ON u.id = rf.usuario_id
WHERE u.correo = 'refugio@huellitasoaxaca.mx'
  AND rf.nombre = 'Refugio Patitas de Oaxaca'
  AND NOT EXISTS (
      SELECT 1 FROM mascotas m WHERE m.refugio_id = rf.id AND m.nombre = 'Max'
  );

INSERT INTO mascotas (
    nombre,
    especie,
    raza,
    sexo,
    edad,
    peso,
    tamano,
    descripcion,
    estado,
    fecha_ingreso,
    imagen,
    refugio_id
)
SELECT
    'Nube',
    'GATO',
    'Doméstico de pelo corto',
    'HEMBRA',
    1,
    3.20,
    'PEQUENO',
    'Nube es curiosa y cariñosa. Prefiere ambientes tranquilos, se adapta a departamentos y disfruta de juegos cortos. Recomendada para familias pacientes con gatos jóvenes.',
    'DISPONIBLE',
    DATE_SUB(CURDATE(), INTERVAL 30 DAY),
    '/media/mascotas/demo-nube.jpg',
    rf.id
FROM refugios rf
INNER JOIN usuarios u ON u.id = rf.usuario_id
WHERE u.correo = 'refugio@huellitasoaxaca.mx'
  AND rf.nombre = 'Refugio Patitas de Oaxaca'
  AND NOT EXISTS (
      SELECT 1 FROM mascotas m WHERE m.refugio_id = rf.id AND m.nombre = 'Nube'
  );

INSERT INTO mascotas (
    nombre,
    especie,
    raza,
    sexo,
    edad,
    peso,
    tamano,
    descripcion,
    estado,
    fecha_ingreso,
    imagen,
    refugio_id
)
SELECT
    'Milo',
    'GATO',
    'Doméstico de pelo corto',
    'MACHO',
    3,
    4.50,
    'PEQUENO',
    'Milo es independiente pero busca caricias por las tardes. Convivencia posible con otros gatos. Bajo mantenimiento y excelente compañero para personas con agenda ocupada.',
    'DISPONIBLE',
    DATE_SUB(CURDATE(), INTERVAL 90 DAY),
    '/media/mascotas/demo-milo.jpg',
    rf.id
FROM refugios rf
INNER JOIN usuarios u ON u.id = rf.usuario_id
WHERE u.correo = 'refugio@huellitasoaxaca.mx'
  AND rf.nombre = 'Refugio Patitas de Oaxaca'
  AND NOT EXISTS (
      SELECT 1 FROM mascotas m WHERE m.refugio_id = rf.id AND m.nombre = 'Milo'
  );

INSERT INTO mascotas (
    nombre,
    especie,
    raza,
    sexo,
    edad,
    peso,
    tamano,
    descripcion,
    estado,
    fecha_ingreso,
    imagen,
    refugio_id
)
SELECT
    'Canela',
    'PERRO',
    'Chihuahua mestiza',
    'HEMBRA',
    5,
    4.80,
    'PEQUENO',
    'Canela es alerta y muy apegada a su cuidador. Perfecta para espacios pequeños si recibe paseos diarios. Salud estable y temperamento equilibrado con adultos.',
    'DISPONIBLE',
    DATE_SUB(CURDATE(), INTERVAL 20 DAY),
    '/media/mascotas/demo-canela.jpg',
    rf.id
FROM refugios rf
INNER JOIN usuarios u ON u.id = rf.usuario_id
WHERE u.correo = 'refugio@huellitasoaxaca.mx'
  AND rf.nombre = 'Refugio Patitas de Oaxaca'
  AND NOT EXISTS (
      SELECT 1 FROM mascotas m WHERE m.refugio_id = rf.id AND m.nombre = 'Canela'
  );

INSERT INTO mascotas (
    nombre,
    especie,
    raza,
    sexo,
    edad,
    peso,
    tamano,
    descripcion,
    estado,
    fecha_ingreso,
    imagen,
    refugio_id
)
SELECT
    'Bruno',
    'PERRO',
    'Pastor mestizo',
    'MACHO',
    6,
    22.00,
    'MEDIANO',
    'Bruno es un adulto sereno, ideal para adopción responsable de mascotas maduras. Nivel de energía moderado, buen guardián y se adapta a hogares con patio.',
    'DISPONIBLE',
    DATE_SUB(CURDATE(), INTERVAL 75 DAY),
    '/media/mascotas/demo-bruno.jpg',
    rf.id
FROM refugios rf
INNER JOIN usuarios u ON u.id = rf.usuario_id
WHERE u.correo = 'refugio@huellitasoaxaca.mx'
  AND rf.nombre = 'Refugio Patitas de Oaxaca'
  AND NOT EXISTS (
      SELECT 1 FROM mascotas m WHERE m.refugio_id = rf.id AND m.nombre = 'Bruno'
  );

-- 4) Imágenes principales en galería
INSERT INTO imagenes_mascotas (url, mascota_id, principal)
SELECT m.imagen, m.id, TRUE
FROM mascotas m
INNER JOIN refugios rf ON rf.id = m.refugio_id
INNER JOIN usuarios u ON u.id = rf.usuario_id
WHERE u.correo = 'refugio@huellitasoaxaca.mx'
  AND rf.nombre = 'Refugio Patitas de Oaxaca'
  AND m.nombre IN ('Luna', 'Max', 'Nube', 'Milo', 'Canela', 'Bruno')
  AND m.imagen IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM imagenes_mascotas im
      WHERE im.mascota_id = m.id
        AND im.url = m.imagen
  );
