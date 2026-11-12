import { useState, useEffect } from 'react'
import '../../styles/estilosReportesAdmin.css'
import StatCard from './StatCard'
import { pesos } from '../../utils/storage'

function ReportesAdmin() {
  // Reportes que vienen del backend (Gateway -> ms-administracion)
  const [general, setGeneral] = useState({
    ingresosTotales: 0,
    ventasTotales: 0,
    productoEstrella: null,
    usuariosRegistrados: 0,
  })
  const [ventasPorProducto, setVentasPorProducto] = useState([])
  const [usuariosPorTipo, setUsuariosPorTipo] = useState([])

  useEffect(() => {
    const cargarReportes = async () => {
      try {
        const respuestaGeneral = await fetch(`${import.meta.env.VITE_API_URL}/reportes/general`)
        const respuestaProductos = await fetch(`${import.meta.env.VITE_API_URL}/reportes/ventas-por-producto`)
        const respuestaTipos = await fetch(`${import.meta.env.VITE_API_URL}/reportes/usuarios-por-tipo`)

        if (respuestaGeneral.ok) {
          setGeneral(await respuestaGeneral.json())
        } else {
          console.error('Error al obtener el reporte general del servidor')
        }
        if (respuestaProductos.ok) {
          setVentasPorProducto(await respuestaProductos.json())
        } else {
          console.error('Error al obtener las ventas por producto del servidor')
        }
        if (respuestaTipos.ok) {
          setUsuariosPorTipo(await respuestaTipos.json())
        } else {
          console.error('Error al obtener los usuarios por tipo del servidor')
        }
      } catch (error) {
        console.error('Error conectando con el backend:', error)
      }
    }

    cargarReportes()
  }, [])

  // Ordenamos los productos del más vendido al menos vendido
  const filasProductos = [...ventasPorProducto].sort((a, b) => b.unidadesVendidas - a.unidadesVendidas)

  return (
    <>
      <h2 className="section-title">Reporte general del negocio</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Ingresos totales" valor={pesos(general.ingresosTotales)} />
        <StatCard etiqueta="Ventas totales" valor={general.ventasTotales} />
        <StatCard etiqueta="Producto estrella" valor={general.productoEstrella ?? '-'} />
        <StatCard etiqueta="Usuarios registrados" valor={general.usuariosRegistrados} />
      </div>

      <div className="row g-4">
        <div className="col-lg-7">
          <div className="admin-card h-100">
            <h5>Ventas por producto</h5>
            <div className="table-responsive">
              <table className="table admin-tabla">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Unidades vendidas</th>
                    <th>Ingresos generados</th>
                  </tr>
                </thead>
                <tbody>
                  {filasProductos.map((f) => (
                    <tr key={f.idProducto}>
                      <td>{f.nombreProducto}</td>
                      <td>{f.unidadesVendidas}</td>
                      <td>{pesos(f.ingresosGenerados)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {filasProductos.length === 0 && (
              <p className="text-muted text-center py-4">No hay datos de ventas para generar el reporte.</p>
            )}
          </div>
        </div>

        <div className="col-lg-5">
          <div className="admin-card h-100">
            <h5>Usuarios por tipo</h5>
            <div className="table-responsive">
              <table className="table admin-tabla">
                <thead>
                  <tr>
                    <th>Tipo de usuario</th>
                    <th>Cantidad</th>
                  </tr>
                </thead>
                <tbody>
                  {usuariosPorTipo.map((u) => (
                    <tr key={u.tipoUsuario}>
                      <td>{u.tipoUsuario}</td>
                      <td>{u.cantidad}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {usuariosPorTipo.length === 0 && (
              <p className="text-muted text-center py-4">No hay usuarios registrados para generar el reporte.</p>
            )}
          </div>
        </div>
      </div>
    </>
  )
}

export default ReportesAdmin