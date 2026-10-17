import StatCard from './StatCard'
import { useCart } from '../../context/CartContext'
import { buscarProducto } from '../../data/productos'
import { pesos } from '../../utils/storage'

function ProductosAdmin() {
  const { carrito } = useCart()

  const filas = carrito
    .map((item) => {
      const p = buscarProducto(item.id)
      return p ? { ...p, cantidad: item.cantidad, subtotal: p.precio * item.cantidad } : null
    })
    .filter(Boolean)

  const unidades = filas.reduce((acc, f) => acc + f.cantidad, 0)
  const total = filas.reduce((acc, f) => acc + f.subtotal, 0)

  return (
    <>
      <h2 className="section-title">Catálogo de productos agregados al carrito</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Productos distintos" valor={filas.length} />
        <StatCard etiqueta="Unidades totales" valor={unidades} />
        <StatCard etiqueta="Total carrito" valor={pesos(total)} />
      </div>

      <div className="admin-card">
        <h5>Detalle de productos</h5>
        <div className="table-responsive">
          <table className="table admin-tabla">
            <thead>
              <tr>
                <th>Producto</th>
                <th>Peso</th>
                <th>Precio unitario</th>
                <th>Cantidad</th>
                <th>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              {filas.map((f) => (
                <tr key={f.id}>
                  <td>{f.nombre}</td>
                  <td>{f.peso}</td>
                  <td>{pesos(f.precio)}</td>
                  <td>{f.cantidad}</td>
                  <td>{pesos(f.subtotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {filas.length === 0 && (
          <p className="text-muted text-center py-4">No hay productos agregados al carrito.</p>
        )}
      </div>
    </>
  )
}

export default ProductosAdmin
