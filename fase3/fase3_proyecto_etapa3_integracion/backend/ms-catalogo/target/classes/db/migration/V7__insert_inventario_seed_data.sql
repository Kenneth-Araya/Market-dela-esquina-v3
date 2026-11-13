INSERT INTO bd_catalogo.inventario
(id_producto, stock_actual, stock_minimo)
VALUES
-- Tus 3 productos originales
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Arroz Grado 1 1kg'), 48, 10),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Leche Entera 1L'), 37, 15),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Aceite Vegetal 900ml'), 30, 10),

-- Los 7 productos nuevos
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Fideos Caracoles 400g'), 50, 10),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Salsa de Tomate 200g'), 30, 5),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Queso Gouda Laminado 250g'), 15, 5),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Yogurt Batido Frutilla 125g'), 40, 10),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Bebida Cola 1.5L'), 25, 10),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Agua Mineral Sin Gas 1.6L'), 35, 10),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Jugo Naranja 1L'), 20, 10);