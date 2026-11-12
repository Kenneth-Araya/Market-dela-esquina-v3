import { useState, useEffect } from 'react'
import '../../styles/estilosProductosAdmin.css'
import StatCard from './StatCard'
import { pesos } from '../../utils/storage'

function ProductosAdmin() {
  // Productos e inventario que vienen del backend (Gateway -> ms-catalogo)
  const [productos, setProductos] = useState([])
  const [inventario, setInventario] = useState([])

  useEffect(() => {
    const cargarCatalogo = async () => {
      try {
        const respuestaProductos = await fetch(`${import.meta.env.VITE_API_URL}/productos`)
        const respuestaInventario = await fetch(`${import.meta.env.VITE_API_URL}/inventario`)

        if (respuestaProductos.ok) {
          setProductos(await respuestaProductos.json())
        } else {
          console.error('Error al obtener los productos del servidor')
        }
        if (respuestaInventario.ok) {
          setInventario(await respuestaInventario.json())
        } else {
          console.error('Error al obtener el inventario del servidor')
        }
      } catch (error) {
        console.error('Error conectando con el backend:', error)
      }
    }

    cargarCatalogo()
  }, [])

  // Unimos cada producto con su stock actual
  const filas = productos.map((p) => {
    const stock = inventario.find((i) => i.idProducto === p.id)?.stockActual ?? 0
    return { ...p, stock, subtotal: p.precioProducto * stock }
  })

  const unidades = filas.reduce((acc, f) => acc + f.stock, 0)
  const total = filas.reduce((acc, f) => acc + f.subtotal, 0)

  return (
    <>
      <h2 className="section-title">Catálogo de productos</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Productos distintos" valor={filas.length} />
        <StatCard etiqueta="Unidades en stock" valor={unidades} />
        <StatCard etiqueta="Valor del inventario" valor={pesos(total)} />
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
                <th>Stock</th>
                <th>Valor en stock</th>
              </tr>
            </thead>
            <tbody>
              {filas.map((f) => (
                <tr key={f.id}>
                  <td>{f.nombreProducto}</td>
                  <td>{f.pesoProducto}</td>
                  <td>{pesos(f.precioProducto)}</td>
                  <td>{f.stock}</td>
                  <td>{pesos(f.subtotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {filas.length === 0 && (
          <p className="text-muted text-center py-4">No hay productos en el catálogo.</p>
        )}
      </div>
    </>
  )
}

export default ProductosAdmin