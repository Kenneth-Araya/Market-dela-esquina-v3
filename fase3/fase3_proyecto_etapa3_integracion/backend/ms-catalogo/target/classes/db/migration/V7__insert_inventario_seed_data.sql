INSERT INTO bd_catalogo.inventario
(id_producto, stock_actual, stock_minimo)
VALUES
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Arroz Grado 1 1kg'), 48, 10),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Leche Entera 1L'), 37, 15),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Aceite Vegetal 900ml'), 30, 10);