INSERT INTO bd_catalogo.movimiento_inventario
(id_producto, tipo_movimiento, cantidad_movimiento, motivo_movimiento, id_venta, fecha_movimiento)
VALUES
-- Tus 3 entradas originales
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Arroz Grado 1 1kg'), 'ENTRADA', 50, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Leche Entera 1L'), 'ENTRADA', 40, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Aceite Vegetal 900ml'), 'ENTRADA', 30, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),

-- Las 7 entradas nuevas para completar el catálogo
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Fideos Caracoles 400g'), 'ENTRADA', 50, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Salsa de Tomate 200g'), 'ENTRADA', 30, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Queso Gouda Laminado 250g'), 'ENTRADA', 15, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Yogurt Batido Frutilla 125g'), 'ENTRADA', 40, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Bebida Cola 1.5L'), 'ENTRADA', 25, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Agua Mineral Sin Gas 1.6L'), 'ENTRADA', 35, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Jugo Naranja 1L'), 'ENTRADA', 20, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),

-- Tus salidas por ventas de prueba (Se mantienen intactas)
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Arroz Grado 1 1kg'), 'SALIDA', 2, 'Venta pagada', 1, '2026-10-01 10:30:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Leche Entera 1L'), 'SALIDA', 3, 'Venta pagada', 1, '2026-10-01 10:30:00');