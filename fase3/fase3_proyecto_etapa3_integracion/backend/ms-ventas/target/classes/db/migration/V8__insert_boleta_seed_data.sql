INSERT INTO bd_ventas.boleta
(numero_boleta, id_venta, fecha_emision_boleta, total_boleta)
VALUES
('B-000001', (SELECT id FROM bd_ventas.venta WHERE fecha_venta = '2026-10-01 10:30:00'), '2026-10-01 10:31:00', 6600);