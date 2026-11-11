INSERT INTO bd_administracion.mensaje_contacto
(nombre_contacto, correo_contacto, contenido_contacto, fecha_envio_contacto, estado_contacto)
VALUES
('Pedro González', 'pedro@gmail.com', 'Hola, quisiera saber si hacen despachos a Maipú.', NOW(), 'NUEVO'),
('Juan Pérez', 'juan@gmail.com', 'Mi pedido llegó incompleto, ¿pueden ayudarme?', NOW(), 'LEIDO'),
('Ana Rojas', 'ana@gmail.com', 'Gracias por la rápida atención, todo llegó perfecto.', NOW(), 'RESPONDIDO');