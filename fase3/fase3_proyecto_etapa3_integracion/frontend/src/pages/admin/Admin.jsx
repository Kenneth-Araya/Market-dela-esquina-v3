import { useState, useEffect } from 'react';
import '../../styles/estilosAdmin.css';
import { Link } from 'react-router-dom';
import StatCard from './StatCard';
import { pesos } from '../../utils/storage';

// Convierte una fecha en "Hoy", "Ayer" o "Hace N días"
const textoFecha = (iso) => {
  const inicioDia = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate());
  const dias = Math.round((inicioDia(new Date()) - inicioDia(new Date(iso))) / 86400000);
  if (dias <= 0) return 'Hoy';
  if (dias === 1) return 'Ayer';
  return `Hace ${dias} días`;
};

function Admin() {
  // Estadísticas que vienen del backend (Gateway -> ms-administracion)
  const [dashboard, setDashboard] = useState({ productos: 0, usuarios: 0, ventasMes: 0, ingresosMes: 0 });

  // Actividad reciente: se arma con los usuarios, ventas y mensajes de contacto más nuevos
  const [actividad, setActividad] = useState([]);

  useEffect(() => {
    const cargarDashboard = async () => {
      try {
        const respuesta = await fetch(`${import.meta.env.VITE_API_URL}/reportes/dashboard`);
        if (respuesta.ok) {
          setDashboard(await respuesta.json());
        } else {
          console.error('Error al obtener el dashboard del servidor');
        }
      } catch (error) {
        console.error('Error conectando con el backend:', error);
      }
    };

    cargarDashboard();
  }, []);

  useEffect(() => {
    const cargarActividad = async () => {
      try {
        const respuestaUsuarios = await fetch(`${import.meta.env.VITE_API_URL}/usuarios`);
        const respuestaVentas = await fetch(`${import.meta.env.VITE_API_URL}/ventas`);
        const respuestaContactos = await fetch(`${import.meta.env.VITE_API_URL}/contactos`);

        const eventos = [];

        if (respuestaUsuarios.ok) {
          const usuarios = await respuestaUsuarios.json();
          usuarios.forEach((u) => {
            if (u.fechaRegistroUsuario) {
              eventos.push({
                id: `usuario-${u.id}`,
                texto: `👤 Usuario "${u.nombreUsuario} ${u.apellidosUsuario}" registrado`,
                fecha: u.fechaRegistroUsuario,
              });
            }
          });
        } else {
          console.error('Error al obtener los usuarios del servidor');
        }

        if (respuestaVentas.ok) {
          const ventas = await respuestaVentas.json();
          ventas.forEach((v) => {
            if (v.venta.fechaVenta) {
              eventos.push({
                id: `venta-${v.venta.id}`,
                texto: v.venta.estadoVenta === 'ANULADA'
                  ? `🛒 Venta #${v.venta.id} anulada`
                  : `🛒 Venta #${v.venta.id} realizada por ${pesos(v.venta.totalVenta)}`,
                fecha: v.venta.fechaVenta,
              });
            }
          });
        } else {
          console.error('Error al obtener las ventas del servidor');
        }

        if (respuestaContactos.ok) {
          const contactos = await respuestaContactos.json();
          contactos.forEach((c) => {
            if (c.fechaEnvioContacto) {
              eventos.push({
                id: `contacto-${c.id}`,
                texto: `✉️ Mensaje de contacto de "${c.nombreContacto}"`,
                fecha: c.fechaEnvioContacto,
              });
            }
          });
        } else {
          console.error('Error al obtener los mensajes de contacto del servidor');
        }

        // Dejamos los 5 eventos más recientes
        eventos.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
        setActividad(eventos.slice(0, 5));
      } catch (error) {
        console.error('Error conectando con el backend:', error);
      }
    };

    cargarActividad();
  }, []);

  return (
    <>
      <h2 className="section-title">Bienvenido de vuelta</h2>

      <div className="row g-4 mb-4">
        <StatCard etiqueta="Productos" valor={dashboard.productos} />
        <StatCard etiqueta="Usuarios" valor={dashboard.usuarios} />
        <StatCard etiqueta="Ventas del mes" valor={dashboard.ventasMes} />
        <StatCard etiqueta="Ingresos del mes" valor={pesos(dashboard.ingresosMes)} />
      </div>

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="admin-card h-100">
            <h5>Accesos rápidos</h5>
            <div className="d-grid gap-3 mt-4">
              {/* Botón Principal (Naranja) */}
              <Link to="/admin/productos" className="btn btn-shopnow">
                ➕ Nuevo producto
              </Link>
              {/* Botones Secundarios (Borde Morado) */}
              <Link to="/registro" className="btn btn-secundario">
                ➕ Nuevo usuario
              </Link>
              <Link to="/admin/productos" className="btn btn-secundario">
                📦 Ver todos los productos
              </Link>
              <Link to="/admin/usuarios" className="btn btn-secundario">
                👥 Ver todos los usuarios
              </Link>
            </div>
          </div>
        </div>

        <div className="col-lg-7">
          <div className="admin-card h-100">
            <h5>Actividad reciente</h5>
            <div className="mt-4">
              {actividad.map((a) => (
                <div className="admin-activity-item" key={a.id}>
                  <span>{a.texto}</span>
                  <span className="admin-fecha">{textoFecha(a.fecha)}</span>
                </div>
              ))}
              {actividad.length === 0 && (
                <p className="text-muted text-center py-4">No hay actividad reciente.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default Admin;
