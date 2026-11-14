import { useState, useEffect } from 'react'
import '../../styles/estilosUsuariosAdmin.css'
import { Link } from 'react-router-dom'
import StatCard from './StatCard'

function UsuariosAdmin() {
  // Usuarios que vienen del backend (Gateway -> ms-usuario)
  const [usuarios, setUsuarios] = useState([])

  useEffect(() => {
    const cargarUsuarios = async () => {
      try {
        // Recuperamos el token JWT guardado al iniciar sesión
        const token = localStorage.getItem('token')

        // Consultamos los usuarios enviando el token al backend
        const respuesta = await fetch(`${import.meta.env.VITE_API_URL}/usuarios`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })

        if (respuesta.ok) {
          setUsuarios(await respuesta.json())
        } else if (respuesta.status === 401) {
          console.error('Tu sesión no es válida o ha expirado')
        } else if (respuesta.status === 403) {
          console.error('No tienes permisos para consultar usuarios')
        } else {
          console.error('Error al obtener los usuarios del servidor')
        }
      } catch (error) {
        console.error('Error conectando con el backend:', error)
      }
    }

    cargarUsuarios()
  }, [])

  // El tipo llega en mayúsculas: ADMINISTRADOR, CLIENTE, VENDEDOR
  const contar = (tipo) => usuarios.filter((u) => u.tipoUsuario === tipo).length

  return (
    <>
      <div className="d-flex justify-content-between align-items-center flex-wrap mb-3">
        <h2 className="section-title mb-3 mb-md-0">Usuarios registrados</h2>
        <Link to="/registro" className="btn btn-shopnow">➕ Nuevo usuario</Link>
      </div>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Usuarios totales" valor={usuarios.length} />
        <StatCard etiqueta="Administradores" valor={contar('ADMINISTRADOR')} />
        <StatCard etiqueta="Clientes" valor={contar('CLIENTE')} />
        <StatCard etiqueta="Vendedores" valor={contar('VENDEDOR')} />
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
                <tr key={u.id}>
                  <td>{u.nombreUsuario} {u.apellidosUsuario}</td>
                  <td>{u.runUsuario}</td>
                  <td>{u.correoUsuario}</td>
                  <td>{u.tipoUsuario}</td>
                  <td>{u.regionUsuario} / {u.comunaUsuario}</td>
                  <td>
                    <span className={u.estadoUsuario === 'ACTIVO' ? 'admin-badge-activo' : 'admin-badge-inactivo'}>
                      {u.estadoUsuario}
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