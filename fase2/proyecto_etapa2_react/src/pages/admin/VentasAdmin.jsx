import StatCard from './StatCard'
import { leer, pesos } from '../../utils/storage'

const formatearFecha = (iso) => new Date(iso).toLocaleString('es-CL', { dateStyle: 'short', timeStyle: 'short' })

function VentasAdmin() {
  // Ventas guardadas al presionar "Finalizar Venta" en el carrito
  const ventas = leer('ventas', [])
  const ingresos = ventas.reduce((acc, v) => acc + v.total, 0)
  const promedio = ventas.length ? Math.round(ingresos / ventas.length) : 0
  const ultima = ventas.length ? formatearFecha(ventas[ventas.length - 1].fecha) : '-'

  return (
    <>
      <h2 className="section-title">Historial de ventas</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Ventas totales" valor={ventas.length} />
        <StatCard etiqueta="Ingresos totales" valor={pesos(ingresos)} />
        <StatCard etiqueta="Ticket promedio" valor={pesos(promedio)} />
        <StatCard etiqueta="Última venta" valor={ultima} />
      </div>

      <div className="admin-card">
        <h5>Detalle de ventas</h5>
        <div className="table-responsive">
          <table className="table admin-tabla">
            <thead>
              <tr>
                <th>N° Venta</th>
                <th>Fecha</th>
                <th>Productos</th>
                <th>Total</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {[...ventas].reverse().map((v) => (
                <tr key={v.id}>
                  <td>{v.id}</td>
                  <td>{formatearFecha(v.fecha)}</td>
                  <td>{v.productos.map((p) => `${p.nombre} x${p.cantidad}`).join(', ')}</td>
                  <td>{pesos(v.total)}</td>
                  <td>{v.estado}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {ventas.length === 0 && (
          <p className="text-muted text-center py-4">No hay ventas registradas.</p>
        )}
      </div>
    </>
  )
}

export default VentasAdmin
