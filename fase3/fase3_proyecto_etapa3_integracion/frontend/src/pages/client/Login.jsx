import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../../styles/estilosLogin.css';

function Login() {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState('');
  const [exito, setExito] = useState(false);
  const [cargando, setCargando] = useState(false);

  const navigate = useNavigate();

  const manejarSubmit = async (e) => {
    e.preventDefault();

    // Limpiamos mensajes previos
    setError('');
    setExito(false);

    const correoLimpio = correo.trim().toLowerCase();
    const contrasenaLimpia = contrasena.trim();

    // 1. VERIFICAMOS QUE LOS CAMPOS DEL LOGIN NO ESTÉN VACÍOS
    if (!correoLimpio || !contrasenaLimpia) {
      setError('Por favor completa ambos campos.');
      return;
    }

    // 2. MÁXIMO 100 CARACTERES EN EL CORREO
    if (correoLimpio.length > 100) {
      setError('El correo no puede superar los 100 caracteres.');
      return;
    }

    // 3. VALIDAMOS QUE EL DOMINIO DEL CORREO SEA UNO DE LOS ELEGIDOS (y tenga nombre antes del @)
    const dominiosPermitidos = ['@duoc.cl', '@duocuc.cl', '@profesor.duoc.cl', '@gmail.com'];
    const esDominioValido = dominiosPermitidos.some((dominio) => {
      return correoLimpio.endsWith(dominio) && correoLimpio.indexOf('@') > 0;
    });

    if (!esDominioValido) {
      setError('El correo debe terminar en @duoc.cl, @profesor.duoc.cl o @gmail.com.');
      return;
    }

    // 4. VALIDAMOS LA LONGITUD DE LA CONTRASEÑA (entre 4 y 10 caracteres)
    if (contrasenaLimpia.length < 4 || contrasenaLimpia.length > 10) {
      setError('La contraseña debe tener entre 4 y 10 caracteres.');
      return;
    }

    // 5. LLAMAMOS AL BACKEND (Gateway -> ms-usuario -> Supabase)
    setCargando(true);
    try {
      const respuesta = await fetch(`${import.meta.env.VITE_API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          correoUsuario: correoLimpio,
          contrasenaUsuario: contrasenaLimpia
        })
      });

      if (!respuesta.ok) {
        // Intentamos leer {"mensaje": "..."}; si no es JSON, usamos el texto plano
        const texto = await respuesta.text();
        let mensaje = '';
        try {
          mensaje = JSON.parse(texto).mensaje;
        } catch {
          mensaje = texto;
        }
        console.error('Error del backend:', respuesta.status, texto);
        setError(mensaje || 'Correo o contraseña incorrectos.');
        return;
      }

      const datos = await respuesta.json();

      // 6. GUARDAMOS LA SESIÓN Y AVISAMOS AL NAVBAR
      // Mismas claves que usaba antes, para que el resto de la app no cambie
      const datosUsuario = {
        id: datos.idUsuario,
        nombre: datos.nombreUsuario,
        correo: datos.correoUsuario,
        tipoUsuario: datos.tipoUsuario, // llega en mayúsculas: ADMINISTRADOR, CLIENTE, VENDEDOR
        inicial: datos.correoUsuario.charAt(0).toUpperCase(),
        avatar: 'https://i.pravatar.cc/100?img=68'
      };

      localStorage.setItem('token', datos.token);
      localStorage.setItem('usuarioActivo', JSON.stringify(datosUsuario));
      window.dispatchEvent(new Event('sesionActualizada'));

      setExito(true);

      // Redirigimos al Home luego de un breve instante
      setTimeout(() => {
        navigate('/');
      }, 800);
    } catch (err) {
      console.error('Error de conexión:', err);
      setError('Error al conectar con el servidor. Revisa que el backend esté corriendo.');
    } finally {
      setCargando(false);
    }
  };

  return (
    <main className="contenedor-login pt-5">

      <section id="seccion-login" className="w-100">
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-12 col-md-8 col-lg-6">
              <form
                id="formulario-login"
                className="border rounded p-4 shadow bg-white"
                onSubmit={manejarSubmit}
                noValidate
              >
                <h2 className="text-center mb-4 fw-bold">Login</h2>

                <div className="mb-3">
                  <label htmlFor="correo" className="form-label fw-semibold">
                    Correo electrónico
                  </label>
                  <input
                    type="email"
                    id="correo"
                    className="form-control"
                    placeholder="nombre@duoc.cl"
                    value={correo}
                    onChange={(e) => {
                      setCorreo(e.target.value);
                      if (error) setError('');
                    }}
                  />
                </div>

                <div className="mb-3">
                  <label htmlFor="contrasena" className="form-label fw-semibold">
                    Contraseña
                  </label>
                  <input
                    type="password"
                    id="contrasena"
                    className="form-control"
                    placeholder="Ingrese contraseña (4 a 10 caracteres)"
                    value={contrasena}
                    onChange={(e) => {
                      setContrasena(e.target.value);
                      if (error) setError('');
                    }}
                  />
                </div>

                {/* Mensajes de validación dinámicos */}
                {error && (
                  <div id="mensaje-resultado" className="alert alert-danger mt-3 py-2 text-center" role="alert">
                    {error}
                  </div>
                )}

                {exito && (
                  <div className="alert alert-success mt-3 py-2 text-center" role="alert">
                    ¡Bienvenido! Iniciando sesión...
                  </div>
                )}

                <button type="submit" className="btn btn-morado w-100 mt-4" disabled={cargando}>
                  {cargando ? 'Ingresando...' : 'Iniciar'}
                </button>
              </form>
            </div>
          </div>
        </div>

        {/* FOOTER LOGIN */}
        <footer className="footer-brand text-light pt-4 pb-3">
          <div className="container">
            <div className="row justify-content-center text-center">
              <div className="col-md-4 mb-3">
                <h5>Market De La Esquina</h5>
                <p className="mb-1">📍 Av. Siempre Viva 742, Quilpue</p>
                <p className="mb-1">📞 +56 9 1234 5678</p>
                <p className="mb-0">✉️ contacto@marketdelaesquina.cl</p>
              </div>
            </div>
            <hr className="border-secondary" />
            <p className="text-center mb-0">&copy; 2026 Market De La Esquina. Todos los derechos reservados.</p>
          </div>
        </footer>
      </section>
    </main>
  );
}

export default Login;
