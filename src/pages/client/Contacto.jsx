import { useState } from 'react';
import '../../styles/estilosContacto.css';
import logoMarket from '../../img/logoMarketNuevo.jpg';

function Contacto() {
    // 1. Estados para capturar lo que escribe el usuario 
    const [nombre, setNombre] = useState('');
    const [correo, setCorreo] = useState('');
    const [contenido, setContenido] = useState('');

    // 2. Estados para mostrar alertas de error o de éxito en pantalla
    const [error, setError] = useState('');
    const [exito, setExito] = useState(false);

    // 3. Función que se ejecuta al enviar el formulario
    const manejarEnvio = (e) => {
        e.preventDefault(); // Evita que la página se recargue y se pierdan los datos

        // Reiniciamos alertas previas
        setError('');
        setExito(false);

        const nombreLimpio = nombre.trim();
        const correoLimpio = correo.trim().toLowerCase();
        const contenidoLimpio = contenido.trim();

        // Validación 1: Que no existan campos vacíos
        if (!nombreLimpio || !correoLimpio || !contenidoLimpio) {
            setError('Por favor, completa todos los campos antes de enviar.');
            return;
        }

        // Validación 2: Verificar que termine en uno de los dominios permitidos (sin romper el código)
        const dominiosPermitidos = ['@gmail.com', '@duocuc.cl', '@duoc.cl', '@profesor.duoc.cl'];
        const esCorreoValido = dominiosPermitidos.some((dominio) =>
            correoLimpio.endsWith(dominio) && correoLimpio.indexOf('@') > 0
        );

        if (!esCorreoValido) {
            setError('Ingresa un correo válido (@gmail.com, @duocuc.cl o @profesor.duoc.cl).');
            return;
        }

        // Validación 3: Longitud mínima del mensaje
        if (contenidoLimpio.length < 10) {
            setError('El mensaje es muy corto. Debe tener al menos 10 caracteres.');
            return;
        }

        // Guardamos el mensaje en localStorage
        const mensajesGuardados = JSON.parse(localStorage.getItem('mensajesContacto') || '[]');
        const nuevoMensaje = {
            id: Date.now(),
            nombre: nombreLimpio,
            correo: correoLimpio,
            contenido: contenidoLimpio,
            fecha: new Date().toLocaleDateString('es-CL')
        };
        localStorage.setItem('mensajesContacto', JSON.stringify([...mensajesGuardados, nuevoMensaje]));

        // Mostramos confirmación de éxito y limpiamos el formulario
        setExito(true);
        setNombre('');
        setCorreo('');
        setContenido('');

        // Ocultamos el mensaje de éxito automáticamente después de 8 segundos
        setTimeout(() => {
            setExito(false);
        }, 8000);
    };

    return (
        <main className="container my-5">

            {/* ===== LOGO Y TÍTULO ===== */}
            <div className="text-center mb-5">
                <img
                    src={logoMarket}
                    alt="Logo Minimarket de la Esquina"
                    className="logo-contacto"
                />
                <h2 className="titulo-contacto fw-bold mt-3">Minimarket de la Esquina</h2>
            </div>

            {/* ===== FORMULARIO ===== */}
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-6">
                    <div className="caja-formulario">

                        <div className="encabezado-formulario text-center">
                            <p>FORMULARIO DE CONTACTO</p>
                        </div>

                        <div className="cuerpo-formulario p-4">
                            <form id="formContacto" onSubmit={manejarEnvio} noValidate>

                                {/* CAMPO NOMBRE */}
                                <div className="mb-3">
                                    <label htmlFor="nombre">NOMBRE COMPLETO</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="nombre"
                                        placeholder="Bairon Valencia"
                                        value={nombre}
                                        onChange={(e) => {
                                            setNombre(e.target.value);
                                            if (error) setError('');
                                        }}
                                    />
                                </div>

                                {/* CAMPO CORREO */}
                                <div className="mb-3">
                                    <label htmlFor="correo">CORREO</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        id="correo"
                                        placeholder="bairon@duocuc.cl"
                                        value={correo}
                                        onChange={(e) => {
                                            setCorreo(e.target.value);
                                            if (error) setError('');
                                        }}
                                    />
                                </div>

                                {/* CAMPO CONTENIDO */}
                                <div className="mb-4">
                                    <label htmlFor="contenido">CONTENIDO</label>
                                    <textarea
                                        className="form-control"
                                        id="contenido"
                                        rows="6"
                                        placeholder="Escribe todos los detalles aquí..."
                                        value={contenido}
                                        onChange={(e) => {
                                            setContenido(e.target.value);
                                            if (error) setError('');
                                        }}
                                    ></textarea>
                                </div>

                                <div>
                                    {/* Alerta condicional de Error */}
                                    {error && (
                                        <div className="alert alert-danger text-center py-2 mb-3" role="alert">
                                            {error}
                                        </div>
                                    )}

                                    {/* Alerta condicional de Éxito */}
                                    {exito && (
                                        <div className="alert alert-success text-center py-2 mb-3" role="alert">
                                            ¡Mensaje enviado con éxito! Te responderemos en un plazo de 72 horas habiles.
                                        </div>
                                    )}
                                </div>

                                {/* BOTÓN PARA ENVIAR */}
                                <div className="text-center mt-4">
                                    <button
                                        type="submit"
                                        id="btnEnviar"
                                        className="btn btn-outline-dark"
                                    >
                                        ENVIAR MENSAJE
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    );
}

export default Contacto;