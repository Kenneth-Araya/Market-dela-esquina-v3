import { useState, useEffect } from 'react';
import '../../styles/estilosProductos.css';

function Productos() {
  // 1. Estado para almacenar los productos que vienen del backend
  const [productos, setProductos] = useState([]);

  // 2. Leemos el carrito guardado en localStorage al cargar la vista
  const [carrito, setCarrito] = useState(() => {
    const carritoGuardado = localStorage.getItem('carritoMarket');
    return carritoGuardado ? JSON.parse(carritoGuardado) : [];
  });

  // 3. Efecto para llamar al Backend al entrar a la página (Reemplaza la lista estática)
  useEffect(() => {
    const cargarProductosReales = async () => {
      try {
        const respuesta = await fetch(`${import.meta.env.VITE_API_URL}/productos`);

        if (respuesta.ok) {
          const datos = await respuesta.json();
          setProductos(datos); // Guardamos los datos de la base de datos en el estado
        } else {
          console.error("Error al obtener los productos del servidor");
        }
      } catch (error) {
        console.error("Error conectando con el backend:", error);
      }
    };

    cargarProductosReales();
  }, []);

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
        // Adaptamos los nombres del backend para que el carrito siga funcionando igual
        const productoParaCarrito = {
          id: producto.id,
          nombre: producto.nombreProducto,
          precio: producto.precioProducto,
          img: producto.imagenProducto,
          cantidad: 1
        };
        return [...prevCarrito, productoParaCarrito];
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
        {/* Iteramos sobre el estado 'productos' que viene de la BD */}
        {productos.map((producto) => {
          const cantidadActual = obtenerCantidad(producto.id);

          return (
            <div key={producto.id} className="col-12 col-sm-6 col-md-4 col-lg-3">
              <div className="tarjeta-producto h-100 d-flex flex-column">

                <div className="tarjeta-producto-img-caja">
                  {/* Usamos imagenProducto según el backend */}
                  <img src={`/img/${producto.imagenProducto}`} alt={producto.nombreProducto} />
                </div>

                <h5 className="tarjeta-producto-titulo">{producto.nombreProducto}</h5>

                <div className="tarjeta-meta-info mt-auto">
                  <span>{producto.pesoProducto}</span>
                  <span>
                    <span className="tarjeta-estrella">⭐</span>
                    ({producto.ratingProducto}/5)
                  </span>
                </div>

                <div className="tarjeta-footer mt-2">
                  <p className="tarjeta-producto-precio">
                    ${producto.precioProducto.toLocaleString('es-CL')}
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