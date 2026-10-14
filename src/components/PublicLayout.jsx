import { Outlet, Link } from 'react-router-dom'

//NOS ASEGURAMOS DE IMPORTAR EL CSS DEL HOME
import '../styles/estilosHome.css'

function PublicLayout() {
  return (
    <>
    
      {/* AQUI IMPLEMENTAREMOS LA BARRA DE NAVEGACION (NAVBAR) */}
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
              <Link to="/carrito" className="icon-btn position-relative text-decoration-none d-flex align-items-center justify-content-center" aria-label="Carrito">
                <span style={{ fontSize: '1.3rem' }}>🛒</span>
                <span id="cart-badge" className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none" style={{ fontSize: '0.75rem', border: '2px solid white' }}>
                  0
                </span>
              </Link>
              <button id="boton-login" className="btn btn-signup">Login</button>
              <button id="boton-registro" className="btn btn-signup">Registro</button>
            </div>
          </div>
        </nav>
      </header>

      {/* AQUI REACT INYECTA DINAMICAMENTE HOME, PRODUCTOS ETC. */}
      <main style={{ minHeight: '80vh' }}>
        <Outlet />
      </main>

      {/* AQUI PEGAMOS NUESTRO FOOTER QUE APARECERA EN TODAS LAS PAGINAS */}
      <footer className="footer-brand text-light pt-4 pb-3 mt-5">
        <div className="container">
          <div className="row justify-content-center text-center">
            <div className="col-md-4 mb-3">
              <h5>Market De La Esquina</h5>
              <p className="mb-1">📍 Av. Siempre Viva 742, Santiago</p>
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
