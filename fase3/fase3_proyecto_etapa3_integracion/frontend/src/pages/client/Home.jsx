import { useState, useEffect } from 'react';
import '../../styles/estilosHome.css';

// LA SECCION PRINCIPAL DE BIENVENIDA
function HeroSection() {
  return (
    <section className="container my-4">
      <div className="hero">
        <div className="row align-items-center g-4">
          <div className="col-lg-6">
            <h1 className="hero-title">
              Bienvenido a<br />
              <strong>Minimarket</strong><br />
              <strong>de la Esquina</strong>
            </h1>
            <div className="customer-row">
              <div className="avatars">
                <img src="https://i.pravatar.cc/40?img=32" alt="Cliente 1" />
                <img src="https://i.pravatar.cc/40?img=44" alt="Cliente 2" />
                <img src="https://i.pravatar.cc/40?img=12" alt="Cliente 3" />
              </div>
            </div>
          </div>
          <div className="col-lg-6 text-center">
            <img src="/img/canastaVerduras.jpg" alt="Canasta de verduras frescas" className="hero-img img-fluid" />
          </div>
        </div>
      </div>
    </section>
  );
}

// LOS BANNERS DE PUBLICIDAD
function BannersSection() {
  return (
    <section className="container mb-5">
      <div className="row g-4">
        <div className="col-md-6">
          <div className="banner banner-peach">
            <div className="banner-text">
              <h3>Productos frescos todos los dias</h3>
            </div>
            <img src="/img/carneFresca.jpg" alt="Repartidor con bolsas de productos" />
          </div>
        </div>
        <div className="col-md-6">
          <div className="banner banner-green">
            <div className="banner-text">
              <h3>Un desayuno sano y facil</h3>
            </div>
            <img src="/img/ensalada.jpg" alt="Plato de comida saludable" />
          </div>
        </div>
      </div>
    </section>
  );
}

// CONTENEDOR DE LOS PRODUCTOS DINÁMICOS (Conectado a la BD y al carrito)
function ProductosSection() {

  // 1. Estado para almacenar los productos del backend
  const [productos, setProductos] = useState([]);

  // 2. Leemos el carrito guardado en localStorage al cargar el Home
  const [carrito, setCarrito] = useState(() => {
    const carritoGuardado = localStorage.getItem('carritoMarket');
    return carritoGuardado ? JSON.parse(carritoGuardado) : [];
  });

  // 3. Efecto para llamar al Backend al entrar a la página
  useEffect(() => {
    const cargarProductosReales = async () => {
      try {
        const respuesta = await fetch(`${import.meta.env.VITE_API_URL}/productos`);
        if (respuesta.ok) {
          const datos = await respuesta.json();
          // Mostramos solo los primeros 4 productos en el Home para no saturar la vista
          setProductos(datos.slice(0, 4));
        } else {
          console.error("Error al obtener los productos del servidor");
        }
      } catch (error) {
        console.error("Error conectando con el backend:", error);
      }
    };

    cargarProductosReales();
  }, []);

  // 4. Guardamos en localStorage y avisamos al contador del Navbar en cada cambio
  useEffect(() => {
    localStorage.setItem('carritoMarket', JSON.stringify(carrito));
    window.dispatchEvent(new Event('carritoActualizado'));
  }, [carrito]);

  // Obtener la cantidad actual de un producto específico
  const obtenerCantidad = (id) => {
    const item = carrito.find((prod) => prod.id === id);
    return item ? item.cantidad : 0;
  };

  // Sumar 1 unidad al producto (+1)
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
        // Adaptamos los nombres de la BD al formato del carrito
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

  // Restar 1 unidad (-1) o quitar del carrito si llega a 0
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
    <section className="container mb-5" id="productos">
      <div className="d-flex justify-content-between align-items-center flex-wrap mb-4">
        <h2 className="section-title mb-3 mb-md-0">Productos Destacados</h2>
      </div>

      <div className="row g-4">
        {productos.map((producto) => {
          const cantidadActual = obtenerCantidad(producto.id);

          return (
            <div className="col-12 col-sm-6 col-md-4 col-lg-3" key={producto.id}>
              <div className="product-card d-flex flex-column h-100">
                {/* Ruta de imagen corregida para que lea desde public/img/ */}
                <img 
                  src={`/img/${producto.imagenProducto}`} 
                  alt={producto.nombreProducto} 
                  className="product-img" 
                />

                <div className="mt-2 flex-grow-1">
                  <h3 className="product-name mb-1">{producto.nombreProducto}</h3>
                  <p className="product-meta mb-3">{producto.pesoProducto}</p>
                </div>

                {/* Contenedor de precio y botones (- cantidad +) usando tu clase .btn-add */}
                <div className="d-flex justify-content-between align-items-center mt-auto">
                  <span className="product-price">
                    ${producto.precioProducto.toLocaleString('es-CL')}
                  </span>

                  <div className="d-flex align-items-center gap-2">
                    {cantidadActual > 0 && (
                      <>
                        <button
                          className="btn-add d-flex align-items-center justify-content-center"
                          onClick={() => disminuirCantidad(producto.id)}
                          aria-label="Disminuir cantidad"
                        >
                          -
                        </button>
                        <span className="fw-bold">{cantidadActual}</span>
                      </>
                    )}

                    <button
                      className="btn-add d-flex align-items-center justify-content-center"
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
    </section>
  );
}

// ==========================================
// FUNCIÓN PRINCIPAL (Lo que React renderiza)
// ==========================================
function Home() {
  return (
    <>
      <HeroSection />
      <BannersSection />
      <ProductosSection />
    </>
  );
}

export default Home;