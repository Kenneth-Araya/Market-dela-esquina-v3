INSERT INTO bd_catalogo.producto
(nombre_producto, descripcion_producto, precio_producto, peso_producto, imagen_producto, rating_producto, activo_producto, id_categoria)
VALUES
-- Categoría: Abarrotes
('Arroz Grado 1 1kg', 'Arroz blanco de grano largo', 1500, '1kg', 'arroz-grado-1.jpg', 4.5, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Abarrotes')),
('Aceite Vegetal 900ml', 'Aceite vegetal para cocina', 2500, '900ml', 'aceite-vegetal.jpg', 4.0, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Abarrotes')),
('Fideos Caracoles 400g', 'Fideos de sémola', 950, '400g', 'fideos-caracoles.jpg', 4.1, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Abarrotes')),
('Salsa de Tomate 200g', 'Salsa de tomate tradicional', 600, '200g', 'salsa-tomate.jpg', 4.3, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Abarrotes')),

-- Categoría: Lácteos
('Leche Entera 1L', 'Leche entera de vaca', 1200, '1L', 'leche-entera.jpg', 4.2, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Lácteos')),
('Queso Gouda Laminado 250g', 'Queso mantecoso en láminas', 2800, '250g', 'queso-gouda.jpg', 4.7, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Lácteos')),
('Yogurt Batido Frutilla 125g', 'Yogurt batido sabor frutilla', 350, '125g', 'yogurt-frutilla.jpg', 4.5, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Lácteos')),

-- Categoría: Bebidas
('Bebida Cola 1.5L', 'Bebida de fantasía sabor cola', 1800, '1.5L', 'bebida-cola.jpg', 4.4, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Bebidas')),
('Agua Mineral Sin Gas 1.6L', 'Agua mineral purificada', 1100, '1.6L', 'agua-mineral.jpg', 4.8, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Bebidas')),
('Jugo Naranja 1L', 'Jugo natural de naranja en caja', 1600, '1L', 'jugo-naranja.jpg', 4.3, TRUE, (SELECT id FROM bd_catalogo.categoria WHERE nombre_categoria = 'Bebidas'));