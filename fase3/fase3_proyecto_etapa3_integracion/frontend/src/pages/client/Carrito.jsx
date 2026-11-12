import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/estilosCarrito.css';

function Carrito() {
    // 1. Cargamos los productos guardados en localStorage
    const [carrito, setCarrito] = useState(() => {
        const carritoGuardado = localStorage.getItem('carritoMarket');
        return carritoGuardado ? JSON.parse(carritoGuardado) : [];
    });

    // Evita que se presione "Finalizar Venta" dos veces mientras el backend responde
    const [procesando, setProcesando] = useState(false);

    const navigate = useNavigate();

    // Cada vez que 'carrito' cambia, guardamos en localStorage y avisamos al Navbar
    useEffect(() => {
        localStorage.setItem('carritoMarket', JSON.stringify(carrito));
        window.dispatchEvent(new Event('carritoActualizado'));
    }, [carrito]);

    // Asegura que el precio siempre sea número (por si venía con signo $ o puntos)
    const obtenerPrecioNumerico = (precio) => {
        if (typeof precio === 'number') return precio;
        return Number(String(precio).replace(/[^0-9]/g, '')) || 0;
    };

    // Sumar 1 unidad al producto (+1)
    const aumentarUnidad = (id) => {
        setCarrito((prevCarrito) =>
            prevCarrito.map((item) =>
                item.id === id ? { ...item, cantidad: item.cantidad + 1 } : item
            )
        );
    };

    // Restar 1 unidad al producto (-1). Si llega a 0, se elimina del carrito
    const disminuirUnidad = (id) => {
        setCarrito((prevCarrito) =>
            prevCarrito
                .map((item) =>
                    item.id === id ? { ...item, cantidad: item.cantidad - 1 } : item
                )
                .filter((item) => item.cantidad > 0)
        );
    };

    // Quitar todas las unidades de ese producto de una sola vez
    const eliminarProductoCompleto = (id) => {
        setCarrito((prevCarrito) => prevCarrito.filter((item) => item.id !== id));
    };

    const vaciarCarrito = () => {
        if (carrito.length === 0) return;
        setCarrito([]);
    };

    // Lee {"mensaje": "..."} del backend; si no es JSON, usa el texto plano
    const leerMensajeError = async (respuesta) => {
        const texto = await respuesta.text();
        let mensaje = '';
        try {
            mensaje = JSON.parse(texto).mensaje;
        } catch {
            mensaje = texto;
        }
        console.error('Error del backend:', respuesta.status, texto);
        return mensaje || 'No se pudo completar la compra.';
    };

    const finalizarCompra = async () => {
        if (carrito.length === 0) {
            alert('Tu carrito está vacío. Agrega productos antes de finalizar la venta.');
            return;
        }

        // La venta se registra a nombre del usuario que inició sesión en el Login
        const sesionGuardada = localStorage.getItem('usuarioActivo');
        const usuario = sesionGuardada ? JSON.parse(sesionGuardada) : null;
        if (!usuario) {
            alert('Debes iniciar sesión para finalizar la compra.');
            navigate('/login');
            return;
        }

        // Gateway -> ms-ventas (-> ms-catalogo para descontar el stock)
        setProcesando(true);
        try {
            const base = `${import.meta.env.VITE_API_URL}`;
            const headers = { 'Content-Type': 'application/json' };

            // 1. Dejamos el carrito del backend igual al que se ve en pantalla
            await fetch(`${base}/carrito/${usuario.id}`, { method: 'DELETE' });

            for (const item of carrito) {
                const respuestaItem = await fetch(`${base}/carrito/${usuario.id}/items`, {
                    method: 'POST',
                    headers,
                    body: JSON.stringify({ idProducto: item.id, cantidad: item.cantidad })
                });
                if (!respuestaItem.ok) {
                    alert(await leerMensajeError(respuestaItem));
                    return;
                }
            }

            // 2. Finalizamos la compra (crea la venta, su detalle y descuenta el stock)
            const respuesta = await fetch(`${base}/ventas/checkout/${usuario.id}`, {
                method: 'POST',
                headers
            });
            if (!respuesta.ok) {
                alert(await leerMensajeError(respuesta));
                return;
            }

            alert('¡Compra realizada con éxito! Gracias por preferir Market De La Esquina.');
            setCarrito([]);
        } catch (err) {
            console.error('Error de conexión:', err);
            alert('Error al conectar con el servidor. Revisa que el backend esté corriendo.');
        } finally {
            setProcesando(false);
        }
    };

    const calcularTotal = () => {
        return carrito.reduce(
            (total, item) => total + obtenerPrecioNumerico(item.precio) * item.cantidad,
            0
        );
    };

    return (
        <main className="container my-5 contenedor-main-carrito">

            <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <h2 className="section-title mb-0 text-brand fw-bold">Tu Carrito de Compras</h2>
                <a href="/productos" className="btn-finalizar-venta mt-3 px-4">
                    ← Seguir Comprando
                </a>
            </div>

            <div className="row g-4">
                {/* Columna Izquierda: Lista de productos */}
                <div className="col-lg-8">
                    <div className="card carrito-card-shadow border-0 p-4 h-100">
                        {carrito.length === 0 ? (
                            <div className="text-center py-5 my-auto">
                                <span className="icono-carrito-vacio">🛒</span>
                                <h4 className="mt-3 text-muted">Tu carrito está vacío</h4>
                            </div>
                        ) : (
                            <div>
                                {carrito.map((item) => {
                                    const precioUnitario = obtenerPrecioNumerico(item.precio);
                                    const subtotalProducto = precioUnitario * item.cantidad;

                                    return (
                                        <div key={item.id} className="item-carrito d-flex align-items-center gap-3 flex-wrap">
                                            {/* Las imágenes viven en public/img/ (igual que en Productos y Home) */}
                                            <img src={`/img/${item.img}`} alt={item.nombre} className="img-item-carrito" />

                                            {/* Información del producto */}
                                            <div className="flex-grow-1">
                                                <h5 className="mb-1 fw-bold">{item.nombre}</h5>
                                                <p className="mb-1 text-muted small">
                                                    ${precioUnitario.toLocaleString('es-CL')} c/u
                                                </p>
                                                <button
                                                    onClick={() => eliminarProductoCompleto(item.id)}
                                                    className="btn-eliminar-todo"
                                                >
                                                    Quitar todo
                                                </button>
                                            </div>

                                            {/* Controles por unidad (- 3 +) igual que en Productos.jsx */}
                                            <div className="carrito-controles-unidad">
                                                <button
                                                    className="btn-circulo-morado"
                                                    onClick={() => disminuirUnidad(item.id)}
                                                    aria-label="Restar una unidad"
                                                >
                                                    -
                                                </button>
                                                <span className="numero-cantidad">{item.cantidad}</span>
                                                <button
                                                    className="btn-circulo-morado"
                                                    onClick={() => aumentarUnidad(item.id)}
                                                    aria-label="Sumar una unidad"
                                                >
                                                    +
                                                </button>
                                            </div>

                                            {/* Subtotal de este producto */}
                                            <div className="fw-bold fs-5 text-brand precio-subtotal-item">
                                                ${subtotalProducto.toLocaleString('es-CL')}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                </div>

                {/* Columna Derecha: Resumen de la Orden */}
                <div className="col-lg-4">
                    <div className="card carrito-card-shadow border-0 p-4 bg-resumen">
                        <h5 className="fw-bold mb-4 text-brand">Resumen de la Orden</h5>

                        <div className="d-flex justify-content-between mb-2">
                            <span className="text-muted">Subtotal</span>
                            <span className="fw-bold">${calcularTotal().toLocaleString('es-CL')}</span>
                        </div>

                        <div className="d-flex justify-content-between mb-2">
                            <span className="text-muted">Envío</span>
                            <span className="fw-bold text-success">Gratis</span>
                        </div>

                        <hr className="my-3" />

                        <div className="d-flex justify-content-between mb-4">
                            <span className="fs-5 fw-bold text-brand">Total</span>
                            <span className="fs-4 fw-bold text-success">
                                ${calcularTotal().toLocaleString('es-CL')}
                            </span>
                        </div>

                        <button
                            className="btn-finalizar-venta w-100 mb-2"
                            onClick={finalizarCompra}
                            disabled={procesando}
                        >
                            Finalizar Venta
                        </button>
                        <button
                            className="btn-vaciar-carrito w-100"
                            onClick={vaciarCarrito}
                        >
                            Vaciar Carrito
                        </button>
                    </div>
                </div>
            </div>
        </main>
    );
}

export default Carrito;
