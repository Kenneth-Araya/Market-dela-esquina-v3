INSERT INTO bd_catalogo.producto
(nombre_producto, descripcion_producto, precio_producto, peso_producto,
 imagen_producto, rating_producto, activo_producto, id_categoria)
VALUES
('Arroz Grado 1 1kg', 'Arroz blanco de grano largo', 1500, '1kg',
 'arroz-grado-1.jpg', 4.5, TRUE,
 (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Abarrotes')),
('Leche Entera 1L', 'Leche entera de vaca', 1200, '1L',
 'leche-entera.jpg', 4.2, TRUE,
 (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Lácteos')),
('Aceite Vegetal 900ml', 'Aceite vegetal para cocina', 2500, '900ml',
 'aceite-vegetal.jpg', 4.0, TRUE,
 (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Abarrotes'));