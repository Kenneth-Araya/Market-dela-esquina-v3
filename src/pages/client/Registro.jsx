import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { DOMINIOS_PERMITIDOS, leer, guardar } from "../../utils/storage";

const regiones = {
  'Región Metropolitana': ['Santiago', 'Maipú', 'Puente Alto', 'Las Condes'],
  'Valparaíso': ['Valparaíso', 'Viña del Mar', 'Quilpué'],
}

const inicial = {
  run: '', nombre: '', apellidos: '', correo: '', fechaNacimiento: '',
  tipoUsuario: '', region: '', comuna: '', direccion: '', contrasena: '', confirmarContrasena: '',
}

// ===== validacion del RUN chileno (modulo 11) =====
function calcularDigitoVerificador(runSinDv) {
  let suma = 0
  let multiplicador = 2
  // de derecha a izquierda, multiplicando por la secuencia 2,3,4,5,6,7
  for (let i = runSinDv.length - 1; i >= 0; i--) {
    suma += parseInt(runSinDv.charAt(i), 10) * multiplicador
    multiplicador = multiplicador === 7 ? 2 : multiplicador + 1
  }
  const resto = 11 - (suma % 11)
  if (resto === 11) return '0'
  if (resto === 10) return 'K'
  return String(resto)
}

function esRunValido(run) {
  if (run.includes('.') || run.includes('-')) return false
  if (run.length < 7 || run.length > 9) return false
  const cuerpo = run.slice(0, -1)
  const dv = run.slice(-1).toUpperCase()
  if (!/^\d+$/.test(cuerpo)) return false
  return dv === calcularDigitoVerificador(cuerpo)
}

function Registro() {
  const navigate = useNavigate()
  const [form, setForm] = useState(inicial)
  const [mensaje, setMensaje] = useState('')

  const cambiar = (e) => {
    const { id, value } = e.target
    // al cambiar la region se reinicia la comuna
    setForm((f) => (id === 'region' ? { ...f, region: value, comuna: '' } : { ...f, [id]: value }))
  }

  const enviar = (e) => {
    e.preventDefault()

    const run = form.run.trim().toUpperCase()
    const nombre = form.nombre.trim()
    const apellidos = form.apellidos.trim()
    const correo = form.correo.trim()
    const direccion = form.direccion.trim()
    const contrasena = form.contrasena.trim()
    const confirmar = form.confirmarContrasena.trim()
    const { tipoUsuario, region, comuna } = form

    if (!run || !nombre || !apellidos || !correo || !tipoUsuario || !region || !comuna || !direccion || !contrasena || !confirmar) {
      return setMensaje('Por favor completa todos los campos obligatorios.')
    }
    if (!esRunValido(run)) {
      return setMensaje('El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion, y un dígito verificador válido.')
    }
    if (nombre.length > 50) return setMensaje('El nombre no puede superar los 50 caracteres.')
    if (apellidos.length > 100) return setMensaje('Los apellidos no pueden superar los 100 caracteres.')
    if (correo.length > 100) return setMensaje('El correo no puede superar los 100 caracteres.')
    if (!DOMINIOS_PERMITIDOS.some((d) => correo.endsWith(d))) {
      return setMensaje('Ingresa un correo @duoc.cl, @profesor.duoc.cl o @gmail.com.')
    }
    if (!['Administrador', 'Cliente', 'Vendedor'].includes(tipoUsuario)) {
      return setMensaje('Selecciona un tipo de usuario válido.')
    }
    if (!regiones[region] || !regiones[region].includes(comuna)) {
      return setMensaje('Selecciona una comuna válida para la región elegida.')
    }
    if (direccion.length > 300) return setMensaje('La dirección no puede superar los 300 caracteres.')
    if (contrasena.length < 4 || contrasena.length > 10) {
      return setMensaje('La contraseña debe tener entre 4 y 10 caracteres.')
    }
    if (contrasena !== confirmar) return setMensaje('Las contraseñas no coinciden.')

    // Guardamos el usuario (sin contraseña) para mostrarlo en el panel de administracion
    const usuarios = leer('usuarios', [])
    usuarios.push({
      run, nombre, apellidos, correo, tipo: tipoUsuario, region, comuna, direccion,
      fechaNacimiento: form.fechaNacimiento, estado: 'Activo',
    })
    guardar('usuarios', usuarios)

    setMensaje('')
    navigate('/login')
  }

  return (
    <div className="pagina-login">
      <main>
        <section id="seccion-registro" className="py-5">
          <div className="container">
            <div className="row justify-content-center">
              <div className="col-12 col-md-9 col-lg-7">
                <form id="formulario-registro" className="border rounded p-4 shadow bg-white" onSubmit={enviar}>
                  <h2 className="text-center mb-4">Registro</h2>

                  <div className="mb-3">
                    <label htmlFor="run" className="form-label">RUN (sin puntos ni guion)</label>
                    <input type="text" id="run" className="form-control" maxLength={9} placeholder="19011022K"
                      autoComplete="off" value={form.run} onChange={cambiar} />
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="nombre" className="form-label">Nombre</label>
                      <input type="text" id="nombre" className="form-control" maxLength={50}
                        placeholder="Ingrese su nombre" autoComplete="given-name"
                        value={form.nombre} onChange={cambiar} />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label htmlFor="apellidos" className="form-label">Apellidos</label>
                      <input type="text" id="apellidos" className="form-control" maxLength={100}
                        placeholder="Ingrese sus apellidos" autoComplete="family-name"
                        value={form.apellidos} onChange={cambiar} />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="correo" className="form-label">Correo electrónico</label>
                    <input type="email" id="correo" className="form-control" maxLength={100}
                      placeholder="nombre@correo.cl" autoComplete="email"
                      value={form.correo} onChange={cambiar} />
                  </div>

                  <div className="mb-3">
                    <label htmlFor="fechaNacimiento" className="form-label">Fecha de nacimiento (opcional)</label>
                    <input type="date" id="fechaNacimiento" className="form-control"
                      value={form.fechaNacimiento} onChange={cambiar} />
                  </div>

                  <div className="mb-3">
                    <label htmlFor="tipoUsuario" className="form-label">Tipo de usuario</label>
                    <select id="tipoUsuario" className="form-select" value={form.tipoUsuario} onChange={cambiar}>
                      <option value="" disabled>Selecciona un tipo de usuario</option>
                      <option value="Administrador">Administrador</option>
                      <option value="Cliente">Cliente</option>
                      <option value="Vendedor">Vendedor</option>
                    </select>
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="region" className="form-label">Región</label>
                      <select id="region" className="form-select" value={form.region} onChange={cambiar}>
                        <option value="" disabled>Selecciona una región</option>
                        {Object.keys(regiones).map((r) => (
                          <option key={r} value={r}>{r}</option>
                        ))}
                      </select>
                    </div>
                    <div className="col-md-6 mb-3">
                      <label htmlFor="comuna" className="form-label">Comuna</label>
                      <select id="comuna" className="form-select" value={form.comuna} onChange={cambiar}
                        disabled={!form.region}>
                        <option value="" disabled>
                          {form.region ? 'Selecciona una comuna' : 'Selecciona primero una región'}
                        </option>
                        {(regiones[form.region] || []).map((c) => (
                          <option key={c} value={c}>{c}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="direccion" className="form-label">Dirección</label>
                    <textarea id="direccion" className="form-control" maxLength={300} rows={2}
                      placeholder="Calle, número, depto/casa" value={form.direccion} onChange={cambiar} />
                  </div>

                  <div className="mb-3">
                    <label htmlFor="contrasena" className="form-label">Contraseña</label>
                    <input type="password" id="contrasena" className="form-control" placeholder="Ingrese contraseña"
                      autoComplete="new-password" value={form.contrasena} onChange={cambiar} />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="confirmarContrasena" className="form-label">Confirmar contraseña</label>
                    <input type="password" id="confirmarContrasena" className="form-control"
                      placeholder="Repita su contraseña" autoComplete="new-password"
                      value={form.confirmarContrasena} onChange={cambiar} />
                  </div>

                  <div className="mt-3" role="alert">
                    {mensaje && <div className="alert alert-danger mb-0">{mensaje}</div>}
                  </div>

                  <button type="submit" className="btn btn-morado w-100 mt-4">Registrarme</button>
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

export default Registro
