// Catalogo unico de productos (antes estaba duplicado en appHome.js y appCarrito.js)
export const productos = [
  { id: 'pan', nombre: 'Pan amasado casero', precio: 1500, peso: '500gm', rating: '4.8/5', img: '/img/pan.jpg' },
  { id: 'queso', nombre: 'Queso gauda laminado', precio: 3200, peso: '250gm', rating: '4.7/5', img: '/img/queso.jpg' },
  { id: 'palta', nombre: 'Palta hass madura', precio: 990, peso: '1 unidad', rating: '4.8/5', img: '/img/palta.jpg' },
  { id: 'leche', nombre: 'Leche entera Colun', precio: 1190, peso: '1L', rating: '4.6/5', img: '/img/leche.jpg' },
]

export const buscarProducto = (id) => productos.find((p) => p.id === id)
