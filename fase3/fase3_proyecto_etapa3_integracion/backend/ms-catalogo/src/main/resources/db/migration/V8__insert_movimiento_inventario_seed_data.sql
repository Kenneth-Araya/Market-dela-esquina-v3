INSERT INTO bd_catalogo.movimiento_inventario
(id_producto, tipo_movimiento, cantidad_movimiento, motivo_movimiento, id_venta, fecha_movimiento)
VALUES
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Arroz Grado 1 1kg'),
 'ENTRADA', 50, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Leche Entera 1L'),
 'ENTRADA', 40, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Aceite Vegetal 900ml'),
 'ENTRADA', 30, 'Ingreso inicial de stock', NULL, '2026-09-28 09:00:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Arroz Grado 1 1kg'),
 'SALIDA', 2, 'Venta pagada', 1, '2026-10-01 10:30:00'),
((SELECT id FROM bd_catalogo.producto WHERE nombre_producto = 'Leche Entera 1L'),
 'SALIDA', 3, 'Venta pagada', 1, '2026-10-01 10:30:00');