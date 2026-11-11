INSERT INTO bd_ventas.detalle_venta
(id_venta, id_producto, nombre_producto, precio_unitario_detalle, cantidad_detalle, subtotal_detalle)
VALUES
((SELECT id FROM bd_ventas.venta WHERE fecha_venta = '2026-10-01 10:30:00'), 1, 'Arroz Grado 1 1kg', 1500, 2, 3000),
((SELECT id FROM bd_ventas.venta WHERE fecha_venta = '2026-10-01 10:30:00'), 2, 'Leche Entera 1L', 1200, 3, 3600),
((SELECT id FROM bd_ventas.venta WHERE fecha_venta = '2026-10-05 18:15:00'), 3, 'Aceite Vegetal 900ml', 2500, 1, 2500),
((SELECT id FROM bd_ventas.venta WHERE fecha_venta = '2026-10-05 18:15:00'), 1, 'Arroz Grado 1 1kg', 1500, 1, 1500),
((SELECT id FROM bd_ventas.venta WHERE fecha_venta = '2026-10-08 12:00:00'), 2, 'Leche Entera 1L', 1200, 2, 2400);