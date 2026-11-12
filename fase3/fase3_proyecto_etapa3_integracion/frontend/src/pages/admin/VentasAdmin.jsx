import { useState, useEffect } from 'react'
import '../../styles/estilosVentasAdmin.css'
import StatCard from './StatCard'
import { pesos } from '../../utils/storage'

const formatearFecha = (iso) => new Date(iso).toLocaleString('es-CL', { dateStyle: 'short', timeStyle: 'short' })

function VentasAdmin() {
  // Ventas y estadísticas que vienen del backend (Gateway -> ms-ventas)
  // Cada venta llega como { venta: {...}, detalles: [...] }
  const [ventas, setVentas] = useState([])
  const [estadisticas, setEstadisticas] = useState({
    ventasTotales: 0,
    ingresosTotales: 0,
    ticketPromedio: 0,
    ultimaVenta: null,
  })

  useEffect(() => {
    const cargarVentas = async () => {
      try {
        const respuestaVentas = await fetch(`${import.meta.env.VITE_API_URL}/ventas`)
        const respuestaEstadisticas = await fetch(`${import.meta.env.VITE_API_URL}/ventas/estadisticas`)

        if (respuestaVentas.ok) {
          setVentas(await respuestaVentas.json())
        } else {
          console.error('Error al obtener las ventas del servidor')
        }
        if (respuestaEstadisticas.ok) {
          setEstadisticas(await respuestaEstadisticas.json())
        } else {
          console.error('Error al obtener las estadísticas de ventas del servidor')
        }
      } catch (error) {
        console.error('Error conectando con el backend:', error)
      }
    }

    cargarVentas()
  }, [])

  const ultima = estadisticas.ultimaVenta ? formatearFecha(estadisticas.ultimaVenta) : '-'

  return (
    <>
      <h2 className="section-title">Historial de ventas</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Ventas totales" valor={estadisticas.ventasTotales} />
        <StatCard etiqueta="Ingresos totales" valor={pesos(estadisticas.ingresosTotales)} />
        <StatCard etiqueta="Ticket promedio" valor={pesos(estadisticas.ticketPromedio)} />
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
              {[...ventas].sort((a, b) => b.venta.id - a.venta.id).map((v) => (
                <tr key={v.venta.id}>
                  <td>{v.venta.id}</td>
                  <td>{formatearFecha(v.venta.fechaVenta)}</td>
                  <td>{v.detalles.map((d) => `${d.nombreProducto} x${d.cantidadDetalle}`).join(', ')}</td>
                  <td>{pesos(v.venta.totalVenta)}</td>
                  <td>{v.venta.estadoVenta}</td>
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