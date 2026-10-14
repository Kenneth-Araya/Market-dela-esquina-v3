import '../../styles/estilosHome.css';



//LA SECCION PRINCIPAL DE BIENVENIDA
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
                        {/* Recuerda poner la carpeta img dentro de la carpeta public de React */}
                        <img src="/img/canastaVerduras.jpg" alt="Canasta de verduras frescas" className="hero-img img-fluid" />
                    </div>
                </div>
            </div>
        </section>
    );
}

//LOS BANNERS DE PUBLICIDAD
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

// CONTENEDOR DE LOS PRODUCTOS DINÁMICOS (Usando tu CSS original)
function ProductosSection() {
  const productos = [
    { id: 1, nombre: "Pan Fresco", descripcion: "1 Kg", precio: "$2.500", imagen: "/img/pan.jpg" },
    { id: 2, nombre: "Queso Laminado", descripcion: "250 gr", precio: "$1.800", imagen: "/img/queso.jpg" },
    { id: 3, nombre: "Palta Hass", descripcion: "1 Kg", precio: "$4.500", imagen: "/img/palta.jpg" },
    { id: 4, nombre: "Leche Natural", descripcion: "1 Litro", precio: "$1.200", imagen: "/img/leche.jpg" }
  ];

  return (
    <section className="container mb-5" id="productos">
      <div className="d-flex justify-content-between align-items-center flex-wrap mb-4">
        {/* Usamos tu clase .section-title */}
        <h2 className="section-title mb-3 mb-md-0">Todos Nuestros Productos</h2>
      </div>

      <div className="row g-4">
        {productos.map((producto) => (
          <div className="col-12 col-sm-6 col-md-4 col-lg-3" key={producto.id}>
            {/* Usamos tu clase .product-card */}
            <div className="product-card d-flex flex-column">
              {/* Usamos tu clase .product-img */}
              <img src={producto.imagen} alt={producto.nombre} className="product-img" />
              
              {/* Usamos tus clases .product-name y .product-meta */}
              <div className="mt-2 flex-grow-1">
                <h3 className="product-name mb-1">{producto.nombre}</h3>
                <p className="product-meta mb-3">{producto.descripcion}</p>
              </div>

              {/* Contenedor de precio y botón usando Flexbox de Bootstrap y tu botón */}
              <div className="d-flex justify-content-between align-items-center mt-auto">
                <span className="product-price">{producto.precio}</span>
                <button className="btn-add d-flex align-items-center justify-content-center">+</button>
              </div>
            </div>
          </div>
        ))}
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
      {/* LLAMAMOS A NUESTRAS 3 PIEZAS EN ORDEN */}
      <HeroSection />
      <BannersSection />
      <ProductosSection />
    </>
  );
}

export default Home;