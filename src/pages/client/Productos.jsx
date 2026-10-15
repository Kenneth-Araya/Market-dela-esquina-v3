import { useState } from 'react';
import '../../styles/estilosProductos.css';

function Productos() {
    const listaProductos = [
        {
            id: 1,
            nombre: 'Pan amasado casero',
            peso: '500gm',
            calificacion: '(4.8/5)',
            precio: '$1.500',
            img: '/img/pan.jpg'
        },
        {
            id: 2,
            nombre: 'Queso gauda laminado',
            peso: '250gm',
            calificacion: '(4.7/5)',
            precio: '$3.200',
            img: '/img/queso.jpg'
        },
        {
            id: 3,
            nombre: 'Palta hass madura',
            peso: '1 unidad',
            calificacion: '(4.8/5)',
            precio: '$990',
            img: '/img/palta.jpg'
        },
        {
            id: 4,
            nombre: 'Leche entera Colun',
            peso: '1L',
            calificacion: '(4.6/5)',
            precio: '$1.190',
            img: '/img/leche.jpg'
        }
    ];

    // Estado que guarda la cantidad de cada producto (dejamos la palta con 1 de ejemplo inicial)
    const [cantidades, setCantidades] = useState({ 3: 1 });

    const aumentarCantidad = (id) => {
        setCantidades((prev) => ({
            ...prev,
            [id]: (prev[id] || 0) + 1
        }));
    };

    const disminuirCantidad = (id) => {
        setCantidades((prev) => ({
            ...prev,
            [id]: Math.max((prev[id] || 0) - 1, 0)
        }));
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
                    const cantidadActual = cantidades[producto.id] || 0;

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
                                    <p className="tarjeta-producto-precio">{producto.precio}</p>

                                    <div className="tarjeta-controles">
                                        {/* Solo muestra el botón - y el número cuando hay al menos 1 agregado */}
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

                                        {/* El botón + redondo y morado siempre está visible */}
                                        <button
                                            className="btn-circulo-morado"
                                            onClick={() => aumentarCantidad(producto.id)}
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