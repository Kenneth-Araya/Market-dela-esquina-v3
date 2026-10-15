import '../../styles/estilosNosotros.css';

function Nosotros() {
    return (
        <>
            {/* ===== PORTADA ===== */}
            <section className="container mt-4 mb-5">
                <div className="nosotros-portada text-center">
                    <h1 className="nosotros-titulo-principal">
                        Conoce Market De La Esquina
                    </h1>
                    {/* Aquí usamos la nueva clase en lugar del style={{...}} */}
                    <p className="nosotros-texto-portada">
                        El minimarket de siempre, ahora también online.
                    </p>
                </div>
            </section>

            {/* ===== ESTADÍSTICAS ===== */}
            <section className="container mb-5">
                <div className="row g-4 text-center">
                    <div className="col-6 col-md-3">
                        <div className="nosotros-stat-card h-100 d-flex flex-column justify-content-center">
                            <span className="nosotros-stat-num">+8</span>
                            {/* Usamos la clase nosotros-stat-label */}
                            <span className="nosotros-stat-label">Años en el barrio</span>
                        </div>
                    </div>
                    <div className="col-6 col-md-3">
                        <div className="nosotros-stat-card h-100 d-flex flex-column justify-content-center">
                            <span className="nosotros-stat-num">100+</span>
                            <span className="nosotros-stat-label">Productos frescos</span>
                        </div>
                    </div>
                    <div className="col-6 col-md-3">
                        <div className="nosotros-stat-card h-100 d-flex flex-column justify-content-center">
                            <span className="nosotros-stat-num">12.5k</span>
                            <span className="nosotros-stat-label">Clientes felices</span>
                        </div>
                    </div>
                    <div className="col-6 col-md-3">
                        <div className="nosotros-stat-card h-100 d-flex flex-column justify-content-center">
                            <span className="nosotros-stat-num">⭐ 4.5</span>
                            <span className="nosotros-stat-label">Reseña promedio</span>
                        </div>
                    </div>
                </div>
            </section>

            {/* ===== NUESTRA EMPRESA ===== */}
            <section className="container mb-5 pb-4">
                <div className="row align-items-center g-5">
                    <div className="col-lg-6 order-2 order-lg-1">
                        <h2 className="section-title">Nuestra Empresa</h2>
                        <p className="nosotros-texto-destacado">
                            Market De La Esquina es un minimarket de barrio dedicado a ofrecer productos de primera
                            necesidad, abarrotes y alimentos frescos a precios convenientes, para que siempre tengas
                            lo que necesitas cerca de casa.
                        </p>
                        <ul className="nosotros-lista-elegante">
                            {/* Usamos la clase icono-lista para el tamaño de los emojis */}
                            <li><span className="icono-lista">🥦</span> Verduras y frutas frescas cada día</li>
                            <li><span className="icono-lista">🚚</span> Entrega a domicilio y retiro fácil</li>
                            <li><span className="icono-lista">🤝</span> Trato cercano, de barrio</li>
                        </ul>
                    </div>
                    <div className="col-lg-6 order-1 order-lg-2 text-center">
                        <img
                            src="/img/canastaVerduras.jpg"
                            alt="Productos frescos"
                            className="nosotros-img-fina"
                        />
                    </div>
                </div>
            </section>

            {/* ===== MISIÓN Y VISIÓN ===== */}
            <section className="container mb-5">
                <div className="nosotros-mv-container">
                    <div className="row">
                        <div className="col-md-5 text-center text-md-start px-4">
                            <h3 className="nosotros-mv-titulo">Misión</h3>
                            <p className="nosotros-texto-destacado m-0">
                                Ofrecer a nuestra comunidad productos de calidad de forma cercana, rápida y
                                accesible, facilitando la compra diaria de abarrotes y comestibles.
                            </p>
                        </div>

                        <div className="col-md-2 d-flex justify-content-center">
                            <div className="nosotros-divisor"></div>
                        </div>

                        <div className="col-md-5 text-center text-md-start px-4">
                            <h3 className="nosotros-mv-titulo">Visión</h3>
                            <p className="nosotros-texto-destacado m-0">
                                Ser el minimarket de referencia del barrio, reconocido por su calidad, cercanía y
                                confianza, creciendo tanto en tienda física como en línea.
                            </p>
                        </div>
                    </div>
                </div>
            </section>
        </>
    );
}

export default Nosotros;