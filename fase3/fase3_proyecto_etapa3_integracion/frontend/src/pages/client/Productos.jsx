import { useState, useEffect } from 'react';
import '../../styles/estilosProductos.css';

function Productos() {
  // Precios numéricos para poder calcular subtotales y totales en el carrito
  const listaProductos = [
    {
      id: 1,
      nombre: 'Pan amasado casero',
      peso: '500gm',
      calificacion: '(4.8/5)',
      precio: 1500,
      img: '/img/pan.jpg'
    },
    {
      id: 2,
      nombre: 'Queso gauda laminado',
      peso: '250gm',
      calificacion: '(4.7/5)',
      precio: 3200,
      img: '/img/queso.jpg'
    },
    {
      id: 3,
      nombre: 'Palta hass madura',
      peso: '1 unidad',
      calificacion: '(4.8/5)',
      precio: 990,
      img: '/img/palta.jpg'
    },
    {
      id: 4,
      nombre: 'Leche entera Colun',
      peso: '1L',
      calificacion: '(4.6/5)',
      precio: 1190,
      img: '/img/leche.jpg'
    }
  ];

  // 1. Leemos el carrito guardado en localStorage al cargar la vista
  const [carrito, setCarrito] = useState(() => {
    const carritoGuardado = localStorage.getItem('carritoMarket');
    return carritoGuardado ? JSON.parse(carritoGuardado) : [];
  });

  // Cada vez que 'carrito' cambia, guardamos en localStorage y avisamos al Navbar
  useEffect(() => {
    localStorage.setItem('carritoMarket', JSON.stringify(carrito));
    window.dispatchEvent(new Event('carritoActualizado'));
  }, [carrito]);

  // Obtener la cantidad actual de un producto específico
  const obtenerCantidad = (id) => {
    const item = carrito.find((prod) => prod.id === id);
    return item ? item.cantidad : 0;
  };

  // Agregar o sumar 1 al producto en el carrito
  const aumentarCantidad = (producto) => {
    setCarrito((prevCarrito) => {
      const existe = prevCarrito.find((item) => item.id === producto.id);
      if (existe) {
        return prevCarrito.map((item) =>
          item.id === producto.id
            ? { ...item, cantidad: item.cantidad + 1 }
            : item
        );
      } else {
        return [...prevCarrito, { ...producto, cantidad: 1 }];
      }
    });
  };

  // Restar 1 o quitar el producto si llega a 0
  const disminuirCantidad = (id) => {
    setCarrito((prevCarrito) =>
      prevCarrito
        .map((item) =>
          item.id === id ? { ...item, cantidad: item.cantidad - 1 } : item
        )
        .filter((item) => item.cantidad > 0)
    );
  };

  return (
    <main className="container mb-5 mt-4 contenedor-main-productos">

      <div className="mb-4">
        <h2 className="section-title mb-3 mb-md-0 titulo-seccion-productos">
          Todos Nuestros Productos
        </h2>
      </div>

      <div className="row g-4">
        {listaProductos.map((producto) => {
          const cantidadActual = obtenerCantidad(producto.id);

          return (
            <div key={producto.id} className="col-12 col-sm-6 col-md-4 col-lg-3">
              <div className="tarjeta-producto h-100 d-flex flex-column">

                <div className="tarjeta-producto-img-caja">
                  <img src={producto.img} alt={producto.nombre} />
                </div>

                <h5 className="tarjeta-producto-titulo">{producto.nombre}</h5>

                <div className="tarjeta-meta-info mt-auto">
                  <span>{producto.peso}</span>
                  <span>
                    <span className="tarjeta-estrella">⭐</span>
                    {producto.calificacion}
                  </span>
                </div>

                <div className="tarjeta-footer mt-2">
                  <p className="tarjeta-producto-precio">
                    ${producto.precio.toLocaleString('es-CL')}
                  </p>

                  <div className="tarjeta-controles">
                    {cantidadActual > 0 && (
                      <>
                        <button
                          className="btn-circulo-morado"
                          onClick={() => disminuirCantidad(producto.id)}
                          aria-label="Disminuir cantidad"
                        >
                          -
                        </button>
                        <span className="numero-cantidad">{cantidadActual}</span>
                      </>
                    )}

                    <button
                      className="btn-circulo-morado"
                      onClick={() => aumentarCantidad(producto)}
                      aria-label="Aumentar cantidad"
                    >
                      +
                    </button>
                  </div>

                </div>
              </div>
            </div>
          );
        })}
      </div>

    </main>
  );
}

export default Productos;