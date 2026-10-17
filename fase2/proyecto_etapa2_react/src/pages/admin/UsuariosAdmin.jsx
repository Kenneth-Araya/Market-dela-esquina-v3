import { Link } from 'react-router-dom'
import StatCard from './StatCard'
import { leer } from '../../utils/storage'

function UsuariosAdmin() {
  // Usuarios guardados desde la pagina de Registro
  const usuarios = leer('usuarios', [])
  const contar = (tipo) => usuarios.filter((u) => u.tipo === tipo).length

  return (
    <>
      <div className="d-flex justify-content-between align-items-center flex-wrap mb-3">
        <h2 className="section-title mb-3 mb-md-0">Usuarios registrados</h2>
        <Link to="/registro" className="btn btn-shopnow">➕ Nuevo usuario</Link>
      </div>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Usuarios totales" valor={usuarios.length} />
        <StatCard etiqueta="Administradores" valor={contar('Administrador')} />
        <StatCard etiqueta="Clientes" valor={contar('Cliente')} />
        <StatCard etiqueta="Vendedores" valor={contar('Vendedor')} />
      </div>

      <div className="admin-card">
        <h5>Detalle de usuarios</h5>
        <div className="table-responsive">
          <table className="table admin-tabla">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>RUN</th>
                <th>Correo</th>
                <th>Tipo</th>
                <th>Región / Comuna</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((u) => (
                <tr key={u.run + u.correo}>
                  <td>{u.nombre} {u.apellidos}</td>
                  <td>{u.run}</td>
                  <td>{u.correo}</td>
                  <td>{u.tipo}</td>
                  <td>{u.region} / {u.comuna}</td>
                  <td>
                    <span className={u.estado === 'Activo' ? 'admin-badge-activo' : 'admin-badge-inactivo'}>
                      {u.estado}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {usuarios.length === 0 && (
          <p className="text-muted text-center py-4">No hay usuarios registrados.</p>
        )}
      </div>
    </>
  )
}

export default UsuariosAdmin
