INSERT INTO bd_ventas.item_carrito
(id_carrito, id_producto, cantidad_item)
VALUES
((SELECT id FROM bd_ventas.carrito WHERE id_usuario = 1 AND estado_carrito = 'ACTIVO'), 3, 2),
((SELECT id FROM bd_ventas.carrito WHERE id_usuario = 1 AND estado_carrito = 'ACTIVO'), 1, 1),
((SELECT id FROM bd_ventas.carrito WHERE id_usuario = 2 AND estado_carrito = 'CONVERTIDO'), 1, 2),
((SELECT id FROM bd_ventas.carrito WHERE id_usuario = 2 AND estado_carrito = 'CONVERTIDO'), 2, 3);