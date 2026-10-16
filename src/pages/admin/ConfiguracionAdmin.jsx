import { useState } from 'react'
import { leer, guardar } from '../../utils/storage'

const porDefecto = {
  nombreTienda: 'Market De La Esquina',
  correoContacto: 'contacto@marketdelaesquina.cl',
  telefonoContacto: '+56 9 1234 5678',
  moneda: 'CLP',
  direccionTienda: 'Av. Siempre Viva 742, Santiago',
  horarioAtencion: 'Lunes a sábado, 9:00 - 20:00',
  notificacionesVentas: false,
  modoMantenimiento: false,
}

function ConfiguracionAdmin() {
  const [config, setConfig] = useState(() => ({ ...porDefecto, ...leer('configuracion', {}) }))
  const [mensaje, setMensaje] = useState(null) // { tipo, texto }

  const cambiar = (e) => {
    const { id, type, value, checked } = e.target
    setConfig((c) => ({ ...c, [id]: type === 'checkbox' ? checked : value }))
  }

  const guardarCambios = (e) => {
    e.preventDefault()
    if (!config.nombreTienda.trim()) {
      return setMensaje({ tipo: 'danger', texto: 'El nombre de la tienda es obligatorio.' })
    }
    if (config.correoContacto.trim() && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(config.correoContacto.trim())) {
      return setMensaje({ tipo: 'danger', texto: 'Ingresa un correo de contacto válido.' })
    }
    guardar('configuracion', config)
    setMensaje({ tipo: 'success', texto: 'Configuración guardada correctamente.' })
  }

  const restaurar = () => {
    setConfig(porDefecto)
    guardar('configuracion', porDefecto)
    setMensaje({ tipo: 'success', texto: 'Se restauraron los valores por defecto.' })
  }

  return (
    <>
      <h2 className="section-title">Configuración de la tienda</h2>

      <div className="admin-form-card">
        <form id="formulario-configuracion" onSubmit={guardarCambios}>
          <div className="mb-3">
            <label htmlFor="nombreTienda" className="form-label">Nombre de la tienda</label>
            <input type="text" id="nombreTienda" className="form-control" maxLength={100}
              placeholder="Market De La Esquina" value={config.nombreTienda} onChange={cambiar} />
          </div>

          <div className="mb-3">
            <label htmlFor="correoContacto" className="form-label">Correo de contacto</label>
            <input type="email" id="correoContacto" className="form-control" maxLength={100}
              placeholder="contacto@marketdelaesquina.cl" value={config.correoContacto} onChange={cambiar} />
          </div>

          <div className="row">
            <div className="col-md-6 mb-3">
              <label htmlFor="telefonoContacto" className="form-label">Teléfono de contacto</label>
              <input type="tel" id="telefonoContacto" className="form-control" maxLength={20}
                placeholder="+56 9 1234 5678" value={config.telefonoContacto} onChange={cambiar} />
            </div>
            <div className="col-md-6 mb-3">
              <label htmlFor="moneda" className="form-label">Moneda</label>
              <select id="moneda" className="form-select" value={config.moneda} onChange={cambiar}>
                <option value="CLP">Peso chileno (CLP)</option>
                <option value="USD">Dólar estadounidense (USD)</option>
                <option value="EUR">Euro (EUR)</option>
              </select>
            </div>
          </div>

          <div className="mb-3">
            <label htmlFor="direccionTienda" className="form-label">Dirección de la tienda</label>
            <textarea id="direccionTienda" className="form-control" maxLength={300} rows={2}
              placeholder="Calle, número, comuna" value={config.direccionTienda} onChange={cambiar} />
          </div>

          <div className="mb-3">
            <label htmlFor="horarioAtencion" className="form-label">Horario de atención</label>
            <input type="text" id="horarioAtencion" className="form-control" maxLength={100}
              placeholder="Lunes a sábado, 9:00 - 20:00" value={config.horarioAtencion} onChange={cambiar} />
          </div>

          <div className="form-check form-switch mb-3">
            <input className="form-check-input" type="checkbox" id="notificacionesVentas"
              checked={config.notificacionesVentas} onChange={cambiar} />
            <label className="form-check-label" htmlFor="notificacionesVentas">
              Recibir notificaciones de nuevas ventas
            </label>
          </div>

          <div className="form-check form-switch mb-4">
            <input className="form-check-input" type="checkbox" id="modoMantenimiento"
              checked={config.modoMantenimiento} onChange={cambiar} />
            <label className="form-check-label" htmlFor="modoMantenimiento">
              Activar modo mantenimiento (oculta la tienda a los clientes)
            </label>
          </div>

          <div className="mt-3" role="alert">
            {mensaje && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}
          </div>

          <div className="d-flex gap-2">
            <button type="submit" className="btn btn-shopnow">Guardar cambios</button>
            <button type="button" className="btn btn-outline-secondary" onClick={restaurar}>
              Restaurar valores por defecto
            </button>
          </div>
        </form>
      </div>
    </>
  )
}

export default ConfiguracionAdmin
