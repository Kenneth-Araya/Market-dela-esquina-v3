import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { DOMINIOS_PERMITIDOS } from '../utils/storage'

function Login() {
  const navigate = useNavigate()
  const [correo, setCorreo] = useState('')
  const [contrasena, setContrasena] = useState('')
  const [mensaje, setMensaje] = useState('')

  const enviar = (e) => {
    e.preventDefault()
    const correoLimpio = correo.trim()
    const claveLimpia = contrasena.trim()

    // 1. Campos obligatorios
    if (!correoLimpio || !claveLimpia) return setMensaje('Por favor completa ambos campos.')

    // 2. Longitud de la contraseña
    if (claveLimpia.length < 4 || claveLimpia.length > 10) {
      return setMensaje('La contraseña debe tener entre 4 y 10 caracteres.')
    }

    // 3. Dominio permitido
    if (!DOMINIOS_PERMITIDOS.some((d) => correoLimpio.endsWith(d))) {
      return setMensaje('El correo debe terminar en @duoc.cl, @profesor.duoc.cl o @gmail.com.')
    }

    // 4. Maximo 100 caracteres
    if (correoLimpio.length > 100) return setMensaje('El correo no puede superar los 100 caracteres.')

    // Si todo esta bien, vamos al home
    setMensaje('')
    navigate('/home')
  }

  return (
    <div className="pagina-login">
      <Link to="/admin" className="admin-link">Admin</Link>
      <main>
        <section id="seccion-login" className="py-5">
          <div className="container">
            <div className="row justify-content-center">
              <div className="col-12 col-md-8 col-lg-6">
                <form id="formulario-login" className="border rounded p-4 shadow bg-white" onSubmit={enviar}>
                  <div className="mb-3">
                    <h2 className="text-center mb-4">Login</h2>
                    <label htmlFor="correo" className="form-label">Correo electrónico</label>
                    <input type="email" id="correo" className="form-control" placeholder="nombre@correo.cl"
                      value={correo} onChange={(e) => setCorreo(e.target.value)} />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="contrasena" className="form-label">Contraseña</label>
                    <input type="password" id="contrasena" className="form-control" placeholder="Ingrese contraseña"
                      value={contrasena} onChange={(e) => setContrasena(e.target.value)} />
                  </div>

                  <div className="mt-3" role="alert">
                    {mensaje && <div className="alert alert-danger mb-0">{mensaje}</div>}
                  </div>

                  <button type="submit" className="btn btn-morado w-100 mt-5">Iniciar</button>
                </form>
              </div>
            </div>
          </div>
        </section>
      </main>

      <footer id="contacto" className="bg-dark text-light text-center py-3">
        <div className="container">
          <p className="mb-1">Venta de comestibles - Viña del Mar</p>
          <p className="mb-0">&copy; 2026 Todos los derechos reservados.</p>
        </div>
      </footer>
    </div>
  )
}

export default Login
