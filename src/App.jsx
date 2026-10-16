import { Routes, Route, Navigate } from 'react-router-dom'
import PublicLayout from './components/PublicLayout'
import AdminLayout from './components/AdminLayout'

// IMPORTAMOS LAS PAGINAS DESDE LA NUEVA CARPETA CLIENT/
// CORRESPONDERIA AL PANEL DEL CLIENTE 
import Home from './pages/client/Home'
import Productos from './pages/client/Productos'
import Nosotros from './pages/client/Nosotros'
import Blogs from './pages/client/Blogs'
import Contacto from './pages/client/Contacto'
import Carrito from './pages/client/Carrito'
import Login from './pages/client/Login'
import Registro from './pages/client/Registro'

//CORRESPONDERIA AL PANEL DEL ADMINISTRADOR
import Admin from './pages/admin/Admin'
import ProductosAdmin from './pages/admin/ProductosAdmin'
import UsuariosAdmin from './pages/admin/UsuariosAdmin'
import VentasAdmin from './pages/admin/VentasAdmin'
import ReportesAdmin from './pages/admin/ReportesAdmin'
import ConfiguracionAdmin from './pages/admin/ConfiguracionAdmin'

function App() {
  return (
    <Routes>
      {/* Paginas con la barra de navegacion publica */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<Navigate to="/home" replace />} />
        <Route path="/home" element={<Home />} />
        <Route path="/productos" element={<Productos />} />
        <Route path="/nosotros" element={<Nosotros />} />
        <Route path="/blogs" element={<Blogs />} />
        <Route path="/contacto" element={<Contacto />} />
      </Route>

      {/* Paginas independientes */}
      <Route path="/carrito" element={<Carrito />} />
      <Route path="/login" element={<Login />} />
      <Route path="/registro" element={<Registro />} />

      {/* Panel de administracion */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<Admin />} />
        <Route path="productos" element={<ProductosAdmin />} />
        <Route path="usuarios" element={<UsuariosAdmin />} />
        <Route path="ventas" element={<VentasAdmin />} />
        <Route path="reportes" element={<ReportesAdmin />} />
        <Route path="configuracion" element={<ConfiguracionAdmin />} />
      </Route>

      <Route path="*" element={<Navigate to="/home" replace />} />
    </Routes>
  )
}

export default App
