ALTER TABLE imagenes_mascotas
    ADD COLUMN principal BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE imagenes_mascotas imagen
JOIN mascotas mascota
  ON mascota.id = imagen.mascota_id
 AND mascota.imagen = imagen.url
LEFT JOIN imagenes_mascotas coincidencia_anterior
  ON coincidencia_anterior.mascota_id = imagen.mascota_id
 AND coincidencia_anterior.url = imagen.url
 AND coincidencia_anterior.id < imagen.id
SET imagen.principal = TRUE
WHERE mascota.imagen IS NOT NULL
  AND TRIM(mascota.imagen) <> ''
  AND coincidencia_anterior.id IS NULL;

INSERT INTO imagenes_mascotas (url, mascota_id, principal)
SELECT mascota.imagen, mascota.id, TRUE
FROM mascotas mascota
WHERE mascota.imagen IS NOT NULL
  AND TRIM(mascota.imagen) <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM imagenes_mascotas imagen
      WHERE imagen.mascota_id = mascota.id
        AND imagen.principal = TRUE
  );

CREATE INDEX idx_mascotas_refugio_estado_fecha_id
    ON mascotas (refugio_id, estado, fecha_ingreso, id);

CREATE INDEX idx_imagenes_mascota_principal_id
    ON imagenes_mascotas (mascota_id, principal, id);
