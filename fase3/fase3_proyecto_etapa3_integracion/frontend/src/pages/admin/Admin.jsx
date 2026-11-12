import { useState, useEffect } from 'react';
import '../../styles/estilosAdmin.css';
import { Link } from 'react-router-dom';
import StatCard from './StatCard';
import { pesos } from '../../utils/storage';

const actividad = [
  { texto: '📦 Producto "Pan amasado casero" agregado', fecha: 'Hoy' },
  { texto: '👤 Usuario "Bairo Valencia" registrado', fecha: 'Ayer' },
  { texto: '👤 Usuario "Kenneth Araya" registrado', fecha: 'Hoy' }, 
  { texto: '🛒 Venta realizada por $12.400', fecha: 'Ayer' },
  { texto: '📦 Producto "Queso gauda laminado" editado', fecha: 'Hace 2 días' },
];

function Admin() {
  // Estadísticas que vienen del backend (Gateway -> ms-administracion)
  const [dashboard, setDashboard] = useState({ productos: 0, usuarios: 0, ventasMes: 0, ingresosMes: 0 });

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
                <div className="admin-activity-item" key={a.texto}>
                  <span>{a.texto}</span>
                  <span className="admin-fecha">{a.fecha}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default Admin;
