UPDATE usuarios usuario
JOIN roles rol ON rol.id = usuario.rol_id
SET usuario.password = '$2a$12$sWH/JPqS4WTSq5hCU8bh0eYdOWM8zJhWQXJBQS5kGBZSQL171o98a'
WHERE usuario.correo = 'admin@huellitasoaxaca.com'
  AND rol.nombre = 'ADMIN'
  AND usuario.password = '$2a$12$2p7pSooLLSOmn95d6swMuehqv1ex.cINLNwgeY68mNLoyokQKv9Li';

UPDATE usuarios usuario
JOIN roles rol ON rol.id = usuario.rol_id
SET usuario.password = '$2a$12$u5MZNErYXkR0i4SjL7hY/.qae3VZn6La7.OEKtqoWRzrWtcwwdMv2'
WHERE usuario.correo = 'refugio@huellitasoaxaca.com'
  AND rol.nombre = 'REFUGIO'
  AND usuario.password = '$2a$12$6/Wga/GgQidoMZ5XRIzxI.ES2KaOG3nnRf9Q.qGDi1k9jiuQonfIa';
