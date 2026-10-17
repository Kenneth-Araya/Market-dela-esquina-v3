import "../../styles/estiloAdmin.css";
import { Link } from 'react-router-dom'
import StatCard from './StatCard'

const actividad = [
  { texto: '📦 Producto "Pan amasado casero" agregado', fecha: 'Hoy' },
  { texto: '👤 Usuario "Bairo Valencia" registrado', fecha: 'Ayer' },
  { texto: '🛒 Venta realizada por $12.400', fecha: 'Ayer' },
  { texto: '📦 Producto "Queso gauda laminado" editado', fecha: 'Hace 2 días' },
]

function Admin() {
  return (
    <>
      <h2 className="section-title">Bienvenido de vuelta</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Productos" valor="24" />
        <StatCard etiqueta="Usuarios" valor="12" />
        <StatCard etiqueta="Ventas del mes" valor="37" />
        <StatCard etiqueta="Ingresos del mes" valor="$482k" />
      </div>

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="admin-card h-100">
            <h5>Accesos rápidos</h5>
            <div className="d-grid gap-2">
              <Link to="/admin/productos" className="btn btn-shopnow">➕ Nuevo producto</Link>
              <Link to="/registro" className="btn btn-signup">➕ Nuevo usuario</Link>
              <Link to="/admin/productos" className="btn btn-signup">📦 Ver todos los productos</Link>
              <Link to="/admin/usuarios" className="btn btn-signup">👥 Ver todos los usuarios</Link>
            </div>
          </div>
        </div>

        <div className="col-lg-7">
          <div className="admin-card h-100">
            <h5>Actividad reciente</h5>
            {actividad.map((a) => (
              <div className="admin-activity-item" key={a.texto}>
                <span>{a.texto}</span>
                <span className="admin-fecha">{a.fecha}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  )
}

export default Admin
