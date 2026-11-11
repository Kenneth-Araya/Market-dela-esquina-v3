INSERT INTO bd_usuarios.usuario
(run_usuario, nombre_usuario, apellidos_usuario, correo_usuario, fecha_nacimiento_usuario,
 tipo_usuario, region_usuario, comuna_usuario, direccion_usuario,
 contrasena_usuario, estado_usuario, fecha_registro_usuario)
VALUES
('761234567', 'Pedro', 'González Soto', 'pedro@gmail.com', '1993-03-01',
 'ADMINISTRADOR', 'Región Metropolitana de Santiago', 'Santiago', 'Calle Santa Pedro 123',
 '$2a$10$REEMPLAZAR_POR_HASH_BCRYPT', 'ACTIVO', NOW()),
('762432453', 'Juan', 'Pérez Rojas', 'juan@gmail.com', '2000-01-26',
 'CLIENTE', 'Región de Valparaíso', 'Viña del Mar', 'Calle Santa Juan 456',
 '$2a$10$REEMPLAZAR_POR_HASH_BCRYPT', 'ACTIVO', NOW());