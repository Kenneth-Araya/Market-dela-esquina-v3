import { Navigate, useLocation } from 'react-router-dom'

const RutaProtegida = ({ children, rolRequerido }) => {
  const location = useLocation()

  // 1. Leemos el usuario guardado en el login
  let usuario = null
  try {
    usuario = JSON.parse(localStorage.getItem('usuarioActivo'))
  } catch {
    usuario = null
  }

  // 2. Si no ha iniciado sesión, va al login (guardamos a dónde quería ir)
  if (!usuario) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  // 3. Si tiene sesión pero no el rol necesario, va al home
  if (rolRequerido && usuario.tipoUsuario !== rolRequerido) {
    return <Navigate to="/home" replace />
  }

  // 4. Si pasa las validaciones, lo dejamos entrar
  return children
}

export default RutaProtegida
