import { NavLink, Outlet } from 'react-router-dom';
import '../styles/estilosHomeAdmin.css'; // <--- CAMBIADO DE ../../ A ../ 

function AdminLayout() {
  return (
    <div className="d-flex admin-layout-bg">
      
      <aside className="p-3 sidebar-admin">
        
        <div className="titulo-sidebar text-center pb-3 mb-4 mt-2">
          <h5 className="mb-0 text-white fw-bold">Panel Administrador</h5>
        </div>

        <div className="d-flex flex-column gap-1">
          <NavLink className="nav-link-admin" to="/admin" end>Inicio</NavLink>
          <NavLink className="nav-link-admin" to="/admin/productos">Productos</NavLink>
          <NavLink className="nav-link-admin" to="/admin/usuarios">Usuarios</NavLink>
          <NavLink className="nav-link-admin" to="/admin/ventas">Ventas</NavLink>
          <NavLink className="nav-link-admin" to="/admin/reportes">Reportes</NavLink>
          <NavLink className="nav-link-admin" to="/admin/configuracion">Configuración</NavLink>
        </div>
        
      </aside>

      <main className="p-4 flex-grow-1">
        <Outlet />
      </main>
      
    </div>
  );
}

export default AdminLayout;