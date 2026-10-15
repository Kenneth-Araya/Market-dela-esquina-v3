import { useState, useEffect } from 'react';
import { Outlet, Link } from 'react-router-dom';

// NOS ASEGURAMOS DE IMPORTAR EL CSS DEL HOME
import '../styles/estilosHome.css';

function PublicLayout() {
  // 1. Estado para guardar la cantidad total de productos en el carrito
  const [totalUnidades, setTotalUnidades] = useState(0);

  // 2. Función que lee el localStorage y suma las cantidades de cada producto
  const calcularUnidadesCarrito = () => {
    const carritoGuardado = localStorage.getItem('carritoMarket');
    if (!carritoGuardado) return 0;

    const carrito = JSON.parse(carritoGuardado);
    return carrito.reduce((total, item) => total + item.cantidad, 0);
  };

  // 3. Efecto que mantiene actualizado el círculo rojo en tiempo real
  useEffect(() => {
    // Calculamos apenas carga la página
    setTotalUnidades(calcularUnidadesCarrito());

    const actualizarBadge = () => {
      setTotalUnidades(calcularUnidadesCarrito());
    };

    // Escuchamos el aviso cuando se agregan o quitan productos en Productos.jsx o Carrito.jsx
    window.addEventListener('carritoActualizado', actualizarBadge);

    return () => {
      window.removeEventListener('carritoActualizado', actualizarBadge);
    };
  }, []);

  return (
    <>
      {/* BARRA DE NAVEGACION (NAVBAR) */}
      <header>
        <nav className="navbar navbar-expand-lg container py-3">
          <Link className="navbar-brand brand" to="/">
            <span className="brand-icon"></span> Market De La Esquina
          </Link>

          <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse justify-content-between" id="navMenu">
            <ul className="navbar-nav mx-auto mb-2 mb-lg-0 gap-lg-4">
              <li className="nav-item"><Link className="nav-link" to="/">Inicio</Link></li>
              <li className="nav-item"><Link className="nav-link" to="/productos">Productos</Link></li>
              <li className="nav-item"><Link className="nav-link" to="/nosotros">Nosotros</Link></li>
              <li className="nav-item"><Link className="nav-link" to="/blogs">Blogs</Link></li>
              <li className="nav-item"><Link className="nav-link" to="/contacto">Contacto</Link></li>
            </ul>

            <div className="d-flex align-items-center gap-3 mt-3 mt-lg-0">
              <Link
                to="/carrito"
                className="icon-btn position-relative text-decoration-none d-flex align-items-center justify-content-center"
                aria-label="Carrito"
              >
                <span>🛒</span>

                {/* Renderizado condicional: Solo muestra el círculo rojo si hay productos (> 0) */}
                {totalUnidades > 0 && (
                  <span
                    id="cart-badge"
                    className="position-absolute top-0 start-100 translate-middle badge rounded-pill badge-carrito-morado"
                  >
                    {totalUnidades}
                  </span>
                )}
              </Link>

              <button id="boton-login" className="btn btn-signup">Login</button>
              <button id="boton-registro" className="btn btn-signup">Registro</button>
            </div>
          </div>
        </nav>
      </header>

      {/* AQUI REACT INYECTA DINAMICAMENTE HOME, PRODUCTOS, CARRITO, ETC. */}
      <main>
        <Outlet />
      </main>

      {/* FOOTER GLOBAL */}
      <footer className="footer-brand text-light pt-4 pb-3 mt-5">
        <div className="container">
          <div className="row justify-content-center text-center">
            <div className="col-md-4 mb-3">
              <h5>Market De La Esquina</h5>
              <p className="mb-1">📍 Av. Siempre Viva 742, Quilpue</p>
              <p className="mb-1">📞 +56 9 1234 5678</p>
              <p className="mb-0">✉️ contacto@marketdelaesquina.cl</p>
            </div>
          </div>
          <hr className="border-secondary" />
          <p className="text-center mb-0">&copy; 2026 Market De La Esquina. Todos los derechos reservados.</p>
        </div>
      </footer>
    </>
  );
}

export default PublicLayout;