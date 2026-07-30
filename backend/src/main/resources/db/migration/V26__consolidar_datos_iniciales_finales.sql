-- Reemplaza exclusivamente las cuentas y los datos sembrados por V13/V25.
-- No usa IDs autoincrementales para localizar registros.

CREATE TEMPORARY TABLE v26_usuarios_provisionales
(
    id BIGINT PRIMARY KEY
);

INSERT INTO v26_usuarios_provisionales (id)
SELECT id
FROM usuarios
WHERE correo IN (
    'admin@huellitasoaxaca.com',
    'refugio@huellitasoaxaca.com',
    'admin@huellitasoaxaca.mx',
    'refugio@huellitasoaxaca.mx',
    'usuario@huellitasoaxaca.mx'
);

CREATE TEMPORARY TABLE v26_refugios_provisionales
(
    id BIGINT PRIMARY KEY
);

INSERT INTO v26_refugios_provisionales (id)
SELECT id
FROM refugios
WHERE usuario_id IN (SELECT id FROM v26_usuarios_provisionales)
   OR (
       nombre = 'Refugio Patitas de Oaxaca'
       AND correo = 'refugio@huellitasoaxaca.mx'
   );

CREATE TEMPORARY TABLE v26_mascotas_provisionales
(
    id BIGINT PRIMARY KEY
);

INSERT INTO v26_mascotas_provisionales (id)
SELECT id
FROM mascotas
WHERE refugio_id IN (SELECT id FROM v26_refugios_provisionales)
   OR imagen LIKE '/media/mascotas/demo-%';

DELETE historial
FROM historial_estado historial
JOIN solicitudes_adopcion solicitud
  ON solicitud.id = historial.solicitud_id
WHERE solicitud.usuario_id IN (SELECT id FROM v26_usuarios_provisionales)
   OR solicitud.mascota_id IN (SELECT id FROM v26_mascotas_provisionales);

DELETE FROM favoritos
WHERE usuario_id IN (SELECT id FROM v26_usuarios_provisionales)
   OR mascota_id IN (SELECT id FROM v26_mascotas_provisionales);

DELETE FROM solicitudes_adopcion
WHERE usuario_id IN (SELECT id FROM v26_usuarios_provisionales)
   OR mascota_id IN (SELECT id FROM v26_mascotas_provisionales);

DELETE FROM donaciones
WHERE usuario_id IN (SELECT id FROM v26_usuarios_provisionales)
   OR refugio_id IN (SELECT id FROM v26_refugios_provisionales);

DELETE FROM tokens_recuperacion_password
WHERE usuario_id IN (SELECT id FROM v26_usuarios_provisionales);

DELETE FROM auditoria_administrativa
WHERE administrador_id IN (SELECT id FROM v26_usuarios_provisionales);

DELETE FROM imagenes_mascotas
WHERE mascota_id IN (SELECT id FROM v26_mascotas_provisionales);

DELETE FROM mascotas
WHERE id IN (SELECT id FROM v26_mascotas_provisionales);

DELETE FROM refugios
WHERE id IN (SELECT id FROM v26_refugios_provisionales);

DELETE FROM usuarios
WHERE id IN (SELECT id FROM v26_usuarios_provisionales);

DROP TEMPORARY TABLE v26_mascotas_provisionales;
DROP TEMPORARY TABLE v26_refugios_provisionales;
DROP TEMPORARY TABLE v26_usuarios_provisionales;

-- Administradores definitivos.
INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Adriana', 'López', 'Cruz', 'adriana.lopez@huellitasoaxaca.mx',
       '$2a$10$0GvJRPf12G7.252ICmwDqugX5YkdvhIaMYvcuTLf9x5VhyU.IbOm.',
       '9514102301', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'adriana.lopez@huellitasoaxaca.mx'
  );

INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Carlos', 'Méndez', 'Ruiz', 'carlos.mendez@huellitasoaxaca.mx',
       '$2a$10$pllcshyJJLadVmimZ/vfjeiH/cmg.EFHOz2DpAy1wnFQi8diXdgs.',
       '9514102302', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'carlos.mendez@huellitasoaxaca.mx'
  );

INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Fernanda', 'García', 'Díaz', 'fernanda.garcia@huellitasoaxaca.mx',
       '$2a$10$X8vIhAJO3KJ3GLt7mNKyfuJjhWJ.gLMyBGlXCDNj1sqEaDe.bxR6q',
       '9514102303', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'fernanda.garcia@huellitasoaxaca.mx'
  );

-- Usuarios normales definitivos.
INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Daniela', 'Hernández', 'Reyes', 'daniela.hernandez@huellitasoaxaca.mx',
       '$2a$10$EkiyQC8IzZdBi2zstbtBIefM5PmWwI2lyqmitz5RaX9bcZQZ8DJia',
       '9514102304', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'USUARIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'daniela.hernandez@huellitasoaxaca.mx'
  );

INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'José', 'Ramírez', 'Soto', 'jose.ramirez@huellitasoaxaca.mx',
       '$2a$10$4p6fq1kUVluycLSkNk1Sn.1nppkrj7ApUKwuKd3.vyDiDZdvHYkkC',
       '9514102305', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'USUARIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'jose.ramirez@huellitasoaxaca.mx'
  );

INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Mariana', 'Pérez', 'Bautista', 'mariana.perez@huellitasoaxaca.mx',
       '$2a$10$/Gper4AP24hN0vKKX.4qH.kJ7IcmRqKZ550YPBE/tjsL5gAcM52ae',
       '9514102306', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'USUARIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'mariana.perez@huellitasoaxaca.mx'
  );

-- Responsables definitivos.
INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Rosa', 'Vásquez', 'López', 'rosa.vasquez@huellitasoaxaca.mx',
       '$2a$10$N9GAtBru2n8ddjIdbFaw.O/MAgbQavp5ghTZqVkwAAmfBXZ768hES',
       '9514102307', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'REFUGIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'rosa.vasquez@huellitasoaxaca.mx'
  );

INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Miguel', 'Cruz', 'Martínez', 'miguel.cruz@huellitasoaxaca.mx',
       '$2a$10$7Mg8qtt72b6ayKzq0CoGj.T3suEHoX/Ja9mSgjlmKqCKY8Q8h3liq',
       '9514102308', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'REFUGIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'miguel.cruz@huellitasoaxaca.mx'
  );

INSERT INTO usuarios (
    nombre, apellido_paterno, apellido_materno, correo, password,
    telefono, activo, fecha_registro, rol_id
)
SELECT 'Elena', 'Sánchez', 'Aquino', 'elena.sanchez@huellitasoaxaca.mx',
       '$2a$10$KMfvE7I.tXgd8H0i5Z9nYuqf9Xh2.VRdTxN8yLm/m89GvQ3X2lhbO',
       '9514102309', TRUE, CURRENT_TIMESTAMP, r.id
FROM roles r
WHERE r.nombre = 'REFUGIO'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios u
      WHERE u.correo = 'elena.sanchez@huellitasoaxaca.mx'
  );

-- Refugios definitivos, aprobados por la primera cuenta ADMIN.
INSERT INTO refugios (
    nombre, descripcion, direccion, telefono, correo, activo, aprobado,
    fecha_aprobacion, aprobado_por, usuario_id
)
SELECT 'Hogar Colitas de Antequera',
       'Organización dedicada al rescate, recuperación y adopción responsable de perros y gatos en Oaxaca de Juárez. Brinda atención básica, seguimiento veterinario y acompañamiento a las familias adoptantes.',
       'Santa Lucía del Camino, Oaxaca', '9514102387',
       'rosa.vasquez@huellitasoaxaca.mx', TRUE, TRUE, CURRENT_TIMESTAMP(6),
       admin.id, responsable.id
FROM usuarios responsable
CROSS JOIN usuarios admin
WHERE responsable.correo = 'rosa.vasquez@huellitasoaxaca.mx'
  AND admin.correo = 'adriana.lopez@huellitasoaxaca.mx'
  AND NOT EXISTS (
      SELECT 1 FROM refugios r
      WHERE r.nombre = 'Hogar Colitas de Antequera'
  );

INSERT INTO refugios (
    nombre, descripcion, direccion, telefono, correo, activo, aprobado,
    fecha_aprobacion, aprobado_por, usuario_id
)
SELECT 'Rescate Animal Monte Albán',
       'Espacio comunitario enfocado en el cuidado temporal de animales en situación de abandono, promoviendo la esterilización, la tenencia responsable y la adopción informada.',
       'San Jacinto Amilpas, Oaxaca', '9512874165',
       'miguel.cruz@huellitasoaxaca.mx', TRUE, TRUE, CURRENT_TIMESTAMP(6),
       admin.id, responsable.id
FROM usuarios responsable
CROSS JOIN usuarios admin
WHERE responsable.correo = 'miguel.cruz@huellitasoaxaca.mx'
  AND admin.correo = 'adriana.lopez@huellitasoaxaca.mx'
  AND NOT EXISTS (
      SELECT 1 FROM refugios r
      WHERE r.nombre = 'Rescate Animal Monte Albán'
  );

INSERT INTO refugios (
    nombre, descripcion, direccion, telefono, correo, activo, aprobado,
    fecha_aprobacion, aprobado_por, usuario_id
)
SELECT 'Patitas del Valle Central',
       'Refugio orientado a la rehabilitación y socialización de perros y gatos rescatados, con atención especial a animales adultos que buscan una segunda oportunidad.',
       'Santa Cruz Xoxocotlán, Oaxaca', '9513659024',
       'elena.sanchez@huellitasoaxaca.mx', TRUE, TRUE, CURRENT_TIMESTAMP(6),
       admin.id, responsable.id
FROM usuarios responsable
CROSS JOIN usuarios admin
WHERE responsable.correo = 'elena.sanchez@huellitasoaxaca.mx'
  AND admin.correo = 'adriana.lopez@huellitasoaxaca.mx'
  AND NOT EXISTS (
      SELECT 1 FROM refugios r
      WHERE r.nombre = 'Patitas del Valle Central'
  );

-- Mascotas definitivas: cuatro por refugio.
INSERT INTO mascotas (
    nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado,
    fecha_ingreso, imagen, refugio_id
)
SELECT 'Luna', 'PERRO', 'Mestiza', 'HEMBRA', 2, 12.50, 'MEDIANO',
       'Sociable, tranquila y acostumbrada a convivir con otros perros. Disfruta los paseos y busca un hogar con espacio para jugar.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/hogar-colitas-antequera/luna.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Hogar Colitas de Antequera'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Luna');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Bruno', 'PERRO', 'Mestizo', 'MACHO', 4, 28.00, 'GRANDE',
       'Noble y protector. Tiene un nivel de energía moderado y responde bien a rutinas de paseo y convivencia familiar.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/hogar-colitas-antequera/bruno.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Hogar Colitas de Antequera'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Bruno');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Nala', 'GATO', 'Doméstica de pelo corto', 'HEMBRA', 1, 3.20, 'PEQUENO',
       'Curiosa y afectuosa. Se adapta con facilidad a espacios interiores y disfruta descansar cerca de las personas.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/hogar-colitas-antequera/nala.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Hogar Colitas de Antequera'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Nala');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Toby', 'GATO', 'Doméstico de pelo corto', 'MACHO', 3, 4.50, 'PEQUENO',
       'Tranquilo e independiente. Convive bien con gatos y prefiere ambientes relajados.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/hogar-colitas-antequera/toby.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Hogar Colitas de Antequera'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Toby');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Canela', 'PERRO', 'Mestiza', 'HEMBRA', 5, 8.50, 'PEQUENO',
       'Cariñosa y de carácter sereno. Es ideal para una familia que busque una compañera adulta y de energía moderada.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/rescate-monte-alban/canela.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Rescate Animal Monte Albán'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Canela');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Max', 'PERRO', 'Cruza de labrador', 'MACHO', 2, 18.00, 'MEDIANO',
       'Juguetón, inteligente y muy activo. Necesita paseos diarios y una familia dispuesta a continuar con su educación.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/rescate-monte-alban/max.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Rescate Animal Monte Albán'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Max');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Kira', 'GATO', 'Doméstica', 'HEMBRA', 2, 3.40, 'PEQUENO',
       'Reservada al principio, pero muy afectuosa cuando adquiere confianza. Se adapta mejor a hogares tranquilos.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/rescate-monte-alban/kira.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Rescate Animal Monte Albán'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Kira');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Simón', 'GATO', 'Doméstico', 'MACHO', 4, 4.80, 'PEQUENO',
       'Amigable, observador y acostumbrado a vivir dentro de casa. Le gusta convivir con personas adultas.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/rescate-monte-alban/simon.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Rescate Animal Monte Albán'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Simón');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Maya', 'PERRO', 'Cruza de pastor', 'HEMBRA', 3, 22.00, 'GRANDE',
       'Activa y obediente. Aprende con rapidez y necesita espacio, ejercicio y actividades de estimulación.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/patitas-valle-central/maya.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Patitas del Valle Central'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Maya');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Rocky', 'PERRO', 'Mestizo', 'MACHO', 6, 20.00, 'MEDIANO',
       'Equilibrado y noble. Disfruta los paseos tranquilos y la compañía de personas.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/patitas-valle-central/rocky.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Patitas del Valle Central'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Rocky');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Mía', 'GATO', 'Doméstica', 'HEMBRA', 1, 3.00, 'PEQUENO',
       'Juguetona, curiosa y sociable. Puede adaptarse a hogares con otras mascotas mediante una presentación gradual.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/patitas-valle-central/mia.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Patitas del Valle Central'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Mía');

INSERT INTO mascotas (nombre, especie, raza, sexo, edad, peso, tamano, descripcion, estado, fecha_ingreso, imagen, refugio_id)
SELECT 'Milo', 'GATO', 'Doméstico', 'MACHO', 3, 4.20, 'PEQUENO',
       'Afectuoso y tranquilo. Busca contacto con las personas y se adapta bien a departamentos.',
       'DISPONIBLE', CURDATE(), '/media/mascotas/demo/patitas-valle-central/milo.jpg', r.id
FROM refugios r
WHERE r.nombre = 'Patitas del Valle Central'
  AND NOT EXISTS (SELECT 1 FROM mascotas m WHERE m.refugio_id = r.id AND m.nombre = 'Milo');

INSERT INTO imagenes_mascotas (url, mascota_id, principal)
SELECT m.imagen, m.id, TRUE
FROM mascotas m
WHERE m.imagen LIKE '/media/mascotas/demo/%'
  AND NOT EXISTS (
      SELECT 1 FROM imagenes_mascotas im
      WHERE im.mascota_id = m.id AND im.url = m.imagen
  );
