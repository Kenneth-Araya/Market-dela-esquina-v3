import '../../styles/estilosBlogs.css';

function Blogs() {
    return (
        <main className="container my-5">
            {/* Título central */}
            <h2 className="text-center fw-bold mb-5 titulo-blogs">NOTICIAS IMPORTANTES</h2>

            <div className="d-flex flex-column gap-5">

                {/* DATO CURIOSO #1 */}
                <div className="card-blog-gris">
                    <div className="row align-items-center">
                        {/* Columna de Texto */}
                        <div className="col-md-6 pe-lg-5">
                            <h3 className="titulo-caso">DATO CURIOSO #1</h3>
                            <p className="parrafo-caso mb-4">
                                <strong>El origen en Quilpué:</strong> Market De La Esquina no nació en una gran oficina
                                corporativa, sino de la necesidad de modernizar el clásico almacén de barrio. Comenzamos
                                digitalizando nuestro stock local para que los vecinos pudieran asegurar su pan amasado
                                justo antes de que saliera del horno.
                            </p>
                            {/* Recuadro limpio */}
                            <div className="p-3 bg-white recuadro-wireframe">
                                <span className="fw-bold texto-marca">💡 Nuestro secreto:</span>
                                <span className="text-dark detalle-curioso"> Tecnología moderna, pero con la misma receta casera
                                    del primer día.</span>
                            </div>
                        </div>
                        {/* Columna de Imagen */}
                        <div className="col-md-6 columna-imagen">
                            <img src="/img/quilpue.jpg" className="img-fluid img-caso" alt="Paisaje de la ciudad" />
                        </div>
                    </div>
                </div>

                {/* DATO CURIOSO #2 */}
                <div className="card-blog-gris">
                    <div className="row align-items-center">
                        {/* Columna de Texto */}
                        <div className="col-md-6 pe-lg-5">
                            <h3 className="titulo-caso">DATO CURIOSO #2</h3>
                            <p className="parrafo-caso mb-4">
                                <strong>Del cuaderno a la nube:</strong> Durante años, los encargos de nuestros vecinos y el
                                clásico "fiado" se anotaban a mano en un gastado cuaderno de espiral. Hoy, hemos dado el
                                salto digital creando nuestra propia plataforma web para gestionar todo de forma más rápida,
                                pero manteniendo intacta la confianza y el trato directo del barrio.
                            </p>
                            {/* Recuadro limpio */}
                            <div className="p-3 bg-white recuadro-wireframe">
                                <span className="fw-bold texto-marca">💻 Nuestro salto digital:</span>
                                <span className="text-dark detalle-curioso"> La misma atención cálida de toda la vida, ahora
                                    respaldada por tecnología moderna y segura.</span>
                            </div>
                        </div>
                        {/* Columna de Imagen */}
                        <div className="col-md-6 columna-imagen">
                            <img src="/img/local.jpg" className="img-fluid img-caso" alt="Interior del minimarket" />
                        </div>
                    </div>
                </div>

            </div>
        </main>
    );
}

export default Blogs;