UPDATE usuarios usuario
JOIN roles rol ON rol.id = usuario.rol_id
SET usuario.password = '$2a$12$2p7pSooLLSOmn95d6swMuehqv1ex.cINLNwgeY68mNLoyokQKv9Li'
WHERE usuario.correo = 'admin@huellitasoaxaca.com'
  AND rol.nombre = 'ADMIN'
  AND usuario.password = '$2a$10$dPVe8.baWzFDCFQHOUoCbulzZcjjMi.WJ5vSHaNYOlQYW.kcL4s7y';

UPDATE usuarios usuario
JOIN roles rol ON rol.id = usuario.rol_id
SET usuario.password = '$2a$12$6/Wga/GgQidoMZ5XRIzxI.ES2KaOG3nnRf9Q.qGDi1k9jiuQonfIa'
WHERE usuario.correo = 'refugio@huellitasoaxaca.com'
  AND rol.nombre = 'REFUGIO'
  AND usuario.password = '$2a$10$B3vQ3Br2kXtrRGMcp0jdeuj2rDcd8cdtnJQFW2BZK2hK8Yrb7lSY6';
