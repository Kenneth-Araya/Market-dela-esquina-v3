import StatCard from './StatCard'
import { leer, pesos } from '../../utils/storage'

function ReportesAdmin() {
  const ventas = leer('ventas', [])
  const usuarios = leer('usuarios', [])

  // Agrupamos las ventas por producto
  const porProducto = {}
  ventas.forEach((v) =>
    v.productos.forEach((p) => {
      const actual = porProducto[p.nombre] || { nombre: p.nombre, unidades: 0, ingresos: 0 }
      actual.unidades += p.cantidad
      actual.ingresos += p.subtotal
      porProducto[p.nombre] = actual
    }),
  )
  const filasProductos = Object.values(porProducto).sort((a, b) => b.unidades - a.unidades)

  // Agrupamos los usuarios por tipo
  const porTipo = {}
  usuarios.forEach((u) => {
    porTipo[u.tipo] = (porTipo[u.tipo] || 0) + 1
  })

  const ingresos = ventas.reduce((acc, v) => acc + v.total, 0)

  return (
    <>
      <h2 className="section-title">Reporte general del negocio</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Ingresos totales" valor={pesos(ingresos)} />
        <StatCard etiqueta="Ventas totales" valor={ventas.length} />
        <StatCard etiqueta="Producto estrella" valor={filasProductos[0]?.nombre ?? '-'} />
        <StatCard etiqueta="Usuarios registrados" valor={usuarios.length} />
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
                    <tr key={f.nombre}>
                      <td>{f.nombre}</td>
                      <td>{f.unidades}</td>
                      <td>{pesos(f.ingresos)}</td>
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
                  {Object.entries(porTipo).map(([tipo, cantidad]) => (
                    <tr key={tipo}>
                      <td>{tipo}</td>
                      <td>{cantidad}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {usuarios.length === 0 && (
              <p className="text-muted text-center py-4">No hay usuarios registrados para generar el reporte.</p>
            )}
          </div>
        </div>
      </div>
    </>
  )
}

export default ReportesAdmin
