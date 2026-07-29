ALTER TABLE usuarios
    MODIFY COLUMN password VARCHAR(255) NULL;

ALTER TABLE usuarios
    ADD COLUMN firebase_uid VARCHAR(128) NULL AFTER correo;

ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_firebase_uid
        UNIQUE (firebase_uid);
