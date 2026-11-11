INSERT INTO bd_usuarios.comuna
(nombre_comuna, id_region)
VALUES
('Santiago', (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región Metropolitana de Santiago')),
('Maipú', (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región Metropolitana de Santiago')),
('Viña del Mar', (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región de Valparaíso')),
('Concepción', (SELECT id FROM bd_usuarios.region WHERE nombre_region = 'Región del Biobío'));