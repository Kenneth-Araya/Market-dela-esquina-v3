import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../../styles/estilosLogin.css';

// Diccionario de regiones y comunas original
const regiones = {
  "Región Metropolitana": ["Santiago", "Maipú", "Puente Alto", "Las Condes"],
  "Valparaíso": ["Valparaíso", "Viña del Mar", "Quilpué"]
};

// ===== Validación del RUN chileno (Módulo 11) =====
function calcularDigitoVerificador(runSinDv) {
  let suma = 0;
  let multiplicador = 2;

  for (let i = runSinDv.length - 1; i >= 0; i--) {
    suma += parseInt(runSinDv.charAt(i), 10) * multiplicador;
    multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
  }

  const resto = 11 - (suma % 11);

  if (resto === 11) return "0";
  if (resto === 10) return "K";
  return resto.toString();
}

function esRunValido(run) {
  // 1. No debe contener puntos ni guion
  if (run.includes(".") || run.includes("-")) return false;

  // 2. Debe tener entre 7 y 9 caracteres
  if (run.length < 7 || run.length > 9) return false;

  // 3. Formato de dígitos terminado en número o K
  if (!/^\d{6,8}[0-9K]$/i.test(run)) return false;

  // 4. Algoritmo Módulo 11
  const cuerpo = run.slice(0, -1);
  const dvIngresado = run.slice(-1).toUpperCase();

  let suma = 0;
  let multiplicador = 2;

  for (let i = cuerpo.length - 1; i >= 0; i--) {
    suma += parseInt(cuerpo.charAt(i), 10) * multiplicador;
    multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
  }

  const resto = suma % 11;
  const dvCalculado = 11 - resto;
  const dvEsperado = dvCalculado === 11 ? '0' : dvCalculado === 10 ? 'K' : String(dvCalculado);

  return dvIngresado === dvEsperado;
}

function Registro() {
  const [run, setRun] = useState('');
  const [nombre, setNombre] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [correo, setCorreo] = useState('');
  const [fechaNacimiento, setFechaNacimiento] = useState('');
  const [tipoUsuario, setTipoUsuario] = useState('');
  const [region, setRegion] = useState('');
  const [comuna, setComuna] = useState('');
  const [direccion, setDireccion] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [confirmarContrasena, setConfirmarContrasena] = useState('');

  const [error, setError] = useState('');
  const [exito, setExito] = useState(false);

  const navigate = useNavigate();

  // Cuando cambia la región, reiniciamos la comuna seleccionada
  const manejarCambioRegion = (e) => {
    setRegion(e.target.value);
    setComuna('');
    if (error) setError('');
  };

  const manejarSubmit = (e) => {
    e.preventDefault();
    setError('');
    setExito(false);

    const runLimpio = run.trim().toUpperCase();
    const nombreLimpio = nombre.trim();
    const apellidosLimpios = apellidos.trim();
    const correoLimpio = correo.trim().toLowerCase();
    const direccionLimpia = direccion.trim();
    const contrasenaLimpia = contrasena.trim();
    const confirmarLimpia = confirmarContrasena.trim();

    // 1. Verificamos que los campos obligatorios no estén vacíos (fecha de nacimiento es opcional)
    if (
      !runLimpio ||
      !nombreLimpio ||
      !apellidosLimpios ||
      !correoLimpio ||
      !tipoUsuario ||
      !region ||
      !comuna ||
      !direccionLimpia ||
      !contrasenaLimpia ||
      !confirmarLimpia
    ) {
      setError('Por favor completa todos los campos obligatorios.');
      return;
    }

    // 2. Validamos el RUN: sin puntos ni guion, entre 7 y 9 caracteres y con dígito verificador correcto
    if (!esRunValido(runLimpio)) {
      setError('El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion, y un dígito verificador válido.');
      return;
    }

    // 3. Validamos la longitud máxima del nombre
    if (nombreLimpio.length > 50) {
      setError('El nombre no puede superar los 50 caracteres.');
      return;
    }

    // 4. Validamos la longitud máxima de los apellidos
    if (apellidosLimpios.length > 100) {
      setError('Los apellidos no pueden superar los 100 caracteres.');
      return;
    }

    // 5. Validamos la longitud máxima del correo
    if (correoLimpio.length > 100) {
      setError('El correo no puede superar los 100 caracteres.');
      return;
    }

    // 6. Validamos que el dominio del correo sea uno de los permitidos (y tenga texto antes del @)
    const dominiosPermitidos = ['@duoc.cl', '@duocuc.cl', '@profesor.duoc.cl', '@gmail.com'];
    const esDominioValido = dominiosPermitidos.some((dominio) => {
      return correoLimpio.endsWith(dominio) && correoLimpio.indexOf('@') > 0;
    });

    if (!esDominioValido) {
      setError('Ingresa un correo @duoc.cl, @profesor.duoc.cl o @gmail.com.');
      return;
    }

    // 7. Validamos que se haya seleccionado un tipo de usuario válido
    if (!['Administrador', 'Cliente', 'Vendedor'].includes(tipoUsuario)) {
      setError('Selecciona un tipo de usuario válido.');
      return;
    }

    // 8. Validamos que la comuna seleccionada pertenezca a la región elegida
    if (!regiones[region] || !regiones[region].includes(comuna)) {
      setError('Selecciona una comuna válida para la región elegida.');
      return;
    }

    // 9. Validamos la longitud máxima de la dirección
    if (direccionLimpia.length > 300) {
      setError('La dirección no puede superar los 300 caracteres.');
      return;
    }

    // 10. Validamos la longitud de la contraseña
    if (contrasenaLimpia.length < 4 || contrasenaLimpia.length > 10) {
      setError('La contraseña debe tener entre 4 y 10 caracteres.');
      return;
    }

    // 11. Validamos que ambas contraseñas coincidan
    if (contrasenaLimpia !== confirmarLimpia) {
      setError('Las contraseñas no coinciden.');
      return;
    }


    // Guardamos el usuario registrado en localStorage para tener respaldo de sus datos
    const usuariosGuardados = JSON.parse(localStorage.getItem('usuariosRegistrados') || '[]');

    // Verificamos que el correo o el RUN no estén ya registrados
    const yaExiste = usuariosGuardados.some(
      (u) => u.correo === correoLimpio || u.run === runLimpio
    );

    if (yaExiste) {
      setError('Ya existe un usuario registrado con ese RUN o correo electrónico.');
      return;
    }

    // Guardamos el nuevo usuario incluyendo su contraseña
    usuariosGuardados.push({
      run: runLimpio,
      nombre: nombreLimpio,
      apellidos: apellidosLimpios,
      correo: correoLimpio,
      fechaNacimiento,
      tipoUsuario,
      region,
      comuna,
      direccion: direccionLimpia,
      contrasena: contrasenaLimpia //GUARDAMOS LA CONTRASEÑA CUANDO REGISTRAMOS
    });

    localStorage.setItem('usuariosRegistrados', JSON.stringify(usuariosGuardados));

    setExito(true);

    // Si todas las validaciones funcionan, redirigimos a la vista de Login ;)
    setTimeout(() => {
      navigate('/login');
    }, 900);
  };

  const comunasDisponibles = region ? regiones[region] || [] : [];

  return (
    <main className="contenedor-login d-flex flex-column justify-content-between min-vh-100 pt-5">
      {/* Botón Admin o Volver al Login en la esquina superior derecha */}
      <Link to="/login" className="admin-link">
        Ir a Login
      </Link>

      <section id="seccion-registro" className="w-100 my-auto py-4">
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-12 col-md-9 col-lg-7">
              <form
                id="formulario-registro"
                className="border rounded p-4 shadow bg-white"
                onSubmit={manejarSubmit}
                noValidate
              >
                <h2 className="text-center mb-4 fw-bold">Registro</h2>

                <div className="mb-3">
                  <label htmlFor="run" className="form-label fw-semibold">
                    RUN (sin puntos ni guion)
                  </label>
                  <input
                    type="text"
                    id="run"
                    className="form-control"
                    maxLength="9"
                    placeholder="19011022K"
                    autoComplete="off"
                    value={run}
                    onChange={(e) => {
                      setRun(e.target.value);
                      if (error) setError('');
                    }}
                  />
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label htmlFor="nombre" className="form-label fw-semibold">
                      Nombre
                    </label>
                    <input
                      type="text"
                      id="nombre"
                      className="form-control"
                      maxLength="50"
                      placeholder="Ingrese su nombre"
                      autoComplete="given-name"
                      value={nombre}
                      onChange={(e) => {
                        setNombre(e.target.value);
                        if (error) setError('');
                      }}
                    />
                  </div>
                  <div className="col-md-6 mb-3">
                    <label htmlFor="apellidos" className="form-label fw-semibold">
                      Apellidos
                    </label>
                    <input
                      type="text"
                      id="apellidos"
                      className="form-control"
                      maxLength="100"
                      placeholder="Ingrese sus apellidos"
                      autoComplete="family-name"
                      value={apellidos}
                      onChange={(e) => {
                        setApellidos(e.target.value);
                        if (error) setError('');
                      }}
                    />
                  </div>
                </div>

                <div className="mb-3">
                  <label htmlFor="correo" className="form-label fw-semibold">
                    Correo electrónico
                  </label>
                  <input
                    type="email"
                    id="correo"
                    className="form-control"
                    maxLength="100"
                    placeholder="nombre@correo.cl"
                    autoComplete="email"
                    value={correo}
                    onChange={(e) => {
                      setCorreo(e.target.value);
                      if (error) setError('');
                    }}
                  />
                </div>

                <div className="mb-3">
                  <label htmlFor="fecha-nacimiento" className="form-label fw-semibold">
                    Fecha de nacimiento (opcional)
                  </label>
                  <input
                    type="date"
                    id="fecha-nacimiento"
                    className="form-control"
                    value={fechaNacimiento}
                    onChange={(e) => setFechaNacimiento(e.target.value)}
                  />
                </div>

                <div className="mb-3">
                  <label htmlFor="tipo-usuario" className="form-label fw-semibold">
                    Tipo de usuario
                  </label>
                  <select
                    id="tipo-usuario"
                    className="form-select"
                    value={tipoUsuario}
                    onChange={(e) => {
                      setTipoUsuario(e.target.value);
                      if (error) setError('');
                    }}
                  >
                    <option value="" disabled>
                      Selecciona un tipo de usuario
                    </option>
                    <option value="Administrador">Administrador</option>
                    <option value="Cliente">Cliente</option>
                    <option value="Vendedor">Vendedor</option>
                  </select>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label htmlFor="region" className="form-label fw-semibold">
                      Región
                    </label>
                    <select
                      id="region"
                      className="form-select"
                      value={region}
                      onChange={manejarCambioRegion}
                    >
                      <option value="" disabled>
                        Selecciona una región
                      </option>
                      {Object.keys(regiones).map((nombreRegion) => (
                        <option key={nombreRegion} value={nombreRegion}>
                          {nombreRegion}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="col-md-6 mb-3">
                    <label htmlFor="comuna" className="form-label fw-semibold">
                      Comuna
                    </label>
                    <select
                      id="comuna"
                      className="form-select"
                      value={comuna}
                      onChange={(e) => {
                        setComuna(e.target.value);
                        if (error) setError('');
                      }}
                      disabled={!region}
                    >
                      <option value="" disabled>
                        {region ? 'Selecciona una comuna' : 'Selecciona primero una región'}
                      </option>
                      {comunasDisponibles.map((nombreComuna) => (
                        <option key={nombreComuna} value={nombreComuna}>
                          {nombreComuna}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="mb-3">
                  <label htmlFor="direccion" className="form-label fw-semibold">
                    Dirección
                  </label>
                  <textarea
                    id="direccion"
                    className="form-control"
                    maxLength="300"
                    rows="2"
                    placeholder="Calle, número, depto/casa"
                    value={direccion}
                    onChange={(e) => {
                      setDireccion(e.target.value);
                      if (error) setError('');
                    }}
                  ></textarea>
                </div>

                <div className="mb-3">
                  <label htmlFor="contrasena" className="form-label fw-semibold">
                    Contraseña
                  </label>
                  <input
                    type="password"
                    id="contrasena"
                    className="form-control"
                    placeholder="Ingrese contraseña"
                    autoComplete="new-password"
                    value={contrasena}
                    onChange={(e) => {
                      setContrasena(e.target.value);
                      if (error) setError('');
                    }}
                  />
                </div>

                <div className="mb-3">
                  <label htmlFor="confirmar-contrasena" className="form-label fw-semibold">
                    Confirmar contraseña
                  </label>
                  <input
                    type="password"
                    id="confirmar-contrasena"
                    className="form-control"
                    placeholder="Repita su contraseña"
                    autoComplete="new-password"
                    value={confirmarContrasena}
                    onChange={(e) => {
                      setConfirmarContrasena(e.target.value);
                      if (error) setError('');
                    }}
                  />
                </div>

                {error && (
                  <div id="mensaje-resultado" className="alert alert-danger mt-3 py-2 text-center" role="alert">
                    {error}
                  </div>
                )}

                {exito && (
                  <div className="alert alert-success mt-3 py-2 text-center" role="alert">
                    ¡Registro completado con éxito! Redirigiendo al Login...
                  </div>
                )}

                <button type="submit" className="btn btn-morado w-100 mt-4">
                  Registrarme
                </button>
              </form>
            </div>
          </div>
        </div>
      </section>

      {/* FOOTER GLOBAL ANCLADO ABAJO */}
      <footer className="footer-brand text-light pt-4 pb-3 w-100 mt-4">
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
    </main>
  );
}

export default Registro;