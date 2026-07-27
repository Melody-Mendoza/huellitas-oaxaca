INSERT INTO roles (nombre, descripcion)
SELECT 'ADMIN', 'Administrador del sistema'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE nombre = 'ADMIN'
);

INSERT INTO roles (nombre, descripcion)
SELECT 'USUARIO', 'Usuario registrado que puede adoptar mascotas'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE nombre = 'USUARIO'
);

INSERT INTO roles (nombre, descripcion)
SELECT 'REFUGIO', 'Responsable de uno o varios refugios'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE nombre = 'REFUGIO'
);