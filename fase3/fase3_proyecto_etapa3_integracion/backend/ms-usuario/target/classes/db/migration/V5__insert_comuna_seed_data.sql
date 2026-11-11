INSERT INTO bd_usuarios.comuna (nombre_comuna, id_region)
VALUES
('Valparaíso',   (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Valparaíso')),
('Viña del Mar', (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Valparaíso')),
('Quilpué',      (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Valparaíso')),
('Santiago',     (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región Metropolitana')),
('Maipú',        (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región Metropolitana')),
('Puente Alto',  (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región Metropolitana')),
('Las Condes',   (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región Metropolitana'));