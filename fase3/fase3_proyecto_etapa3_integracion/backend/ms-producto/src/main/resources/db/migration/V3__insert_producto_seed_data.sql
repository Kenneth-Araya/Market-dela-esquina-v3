INSERT INTO bd_productos.producto 
(codigo_barra, nombre, descripcion, precio_producto, categoria_id)
VALUES 
('7801234567890', 'Bebida Bilz', 'Formato 1.5L. Bebida de fantasía sabor frutal único, ideal para compartir momentos en familia.', 1590, 1),
('7809876543210', 'Aceite Natura', 'Envase 900ml. Aceite 100% puro de maravilla, libre de colesterol y rico en vitamina E.', 2490, 2),
('7805556667771', 'Arroz Tucapel', 'Bolsa 1kg. Arroz grado 1 de grano largo y ancho, asegura un granado perfecto en cada comida.', 1450, 2),
('7804443332220', 'Tallarines Lucchetti', 'Paquete 400g N5. Pasta de sémola de trigos duros, aporta energía y queda al dente en pocos minutos.', 990, 2),
('7801112223334', 'Té Supremo', 'Caja 20 bolsitas. Té negro Ceylán selección especial, destaca por su aroma intenso y color dorado.', 1150, 3)
ON CONFLICT (codigo_barra) DO NOTHING;
-- PARCHE: Sincroniza la secuencia del ID autoincremental después de la inserción forzada
SELECT setval(pg_get_serial_sequence('bd_productos.producto', 'id'), COALESCE(max(id), 1)) FROM bd_productos.producto;