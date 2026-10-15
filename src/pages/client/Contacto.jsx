import '../../styles/estilosContacto.css';

import logoMarket from '../../img/logoMarketNuevo.jpg';

function Contacto() {
    return (
        <main className="container my-5">

            {/* ===== LOGO Y TÍTULO ===== */}
            <div className="text-center mb-5">
                <img 
                src={logoMarket} 
                alt='Logo Minimarket de la Esquina'
                className="logo-contacto" />
                <h2>Minimarket de la Esquina</h2>
            </div>

            {/* ===== FORMULARIO ===== */}
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-6">
                    <div className="caja-formulario">

                        <div className="encabezado-formulario text-center">
                            <p>FORMULARIO DE CONTACTO</p>
                        </div>

                        <div className="cuerpo-formulario p-4">
                            <form id="formContacto">

                                {/* CAMPO NOMBRE */}
                                <div className="mb-3">
                                    <label htmlFor="nombre">NOMBRE COMPLETO</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="nombre"
                                        placeholder="Bairon Valencia"
                                    />
                                </div>

                                {/* CAMPO CORREO */}
                                <div className="mb-3">
                                    <label htmlFor="correo">CORREO</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        id="correo"
                                        placeholder="bairon@duoc.cl"
                                    />
                                </div>

                                {/* CAMPO CONTENIDO */}
                                <div className="mb-4">
                                    <label htmlFor="contenido">CONTENIDO</label>
                                    <textarea
                                        className="form-control"
                                        id="contenido"
                                        rows="8"
                                        placeholder="Escribe todos los detalles aquí..."
                                    ></textarea>
                                </div>

                                {/* BOTÓN PARA ENVIAR */}
                                <div className="text-center mt-4">
                                    <button
                                        type="button"
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