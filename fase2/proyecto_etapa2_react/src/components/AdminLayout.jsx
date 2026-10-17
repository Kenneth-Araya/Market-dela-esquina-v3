import { NavLink, Outlet } from 'react-router-dom'

// TODO: sidebar y topbar del panel de administración
function AdminLayout() {
  return (
    <div className="d-flex">
      <aside className="p-3 border-end" style={{ minWidth: 220 }}>
        <NavLink className="d-block" to="/admin" end>Inicio</NavLink>
        <NavLink className="d-block" to="/admin/productos">Productos</NavLink>
        <NavLink className="d-block" to="/admin/usuarios">Usuarios</NavLink>
        <NavLink className="d-block" to="/admin/ventas">Ventas</NavLink>
        <NavLink className="d-block" to="/admin/reportes">Reportes</NavLink>
        <NavLink className="d-block" to="/admin/configuracion">Configuración</NavLink>
      </aside>
      <main className="p-4 flex-grow-1">
        <Outlet />
      </main>
    </div>
  )
}

export default AdminLayout
