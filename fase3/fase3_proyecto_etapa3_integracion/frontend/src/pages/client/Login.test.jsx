
 // @vitest-environment jsdom

import React from 'react';
import { render, screen, cleanup, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { describe, test, expect, beforeEach, afterEach, vi } from 'vitest';
import Login from './Login.jsx';

// Datos que simularemos recibir desde el backend.
const usuarioBackend = {
  idUsuario: 1,
  nombreUsuario: 'Cliente Prueba',
  correoUsuario: 'cliente@gmail.com',
  tipoUsuario: 'CLIENTE',
  token: 'token-de-prueba'
};

// Simulamos una respuesta exitosa del backend.
function respuestaExitosa() {
  return {
    ok: true,
    json: async () => usuarioBackend
  };
}

// Simulamos una respuesta de error del backend.
function respuestaError(mensaje, status = 401) {
  return {
    ok: false,
    status,
    text: async () => JSON.stringify({ mensaje })
  };
}

// Mostramos el Login con React Router.
function mostrarLogin() {
  const user = userEvent.setup();

  render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<h1>Página principal</h1>} />
      </Routes>
    </MemoryRouter>
  );

  return user;
}

// Función para completar los campos del Login.
async function completarLogin(user, correo = 'cliente@gmail.com', contrasena = '123456') {
  await user.type(screen.getByLabelText('Correo electrónico'), correo);
  await user.type(screen.getByLabelText('Contraseña'), contrasena);
}

// Función para enviar el formulario.
async function enviarLogin(user) {
  await user.click(screen.getByRole('button', { name: 'Iniciar' }));
}

describe('Pruebas unitarias del Login', () => {

  beforeEach(() => {
    // Cada prueba comienza sin una sesión guardada.
    localStorage.clear();

    // Simulamos fetch para no depender del backend real.
    vi.stubGlobal('fetch', vi.fn());

    // Evitamos mostrar errores esperados en la consola.
    vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    cleanup();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  // PRUEBA 1
  test('muestra correctamente el formulario de Login', () => {
    mostrarLogin();

    expect(screen.getByRole('heading', { name: 'Login' })).toBeTruthy();
    expect(screen.getByLabelText('Correo electrónico')).toBeTruthy();
    expect(screen.getByLabelText('Contraseña')).toBeTruthy();
    expect(screen.getByRole('button', { name: 'Iniciar' })).toBeTruthy();
  });

  // PRUEBA 2
  test('muestra error cuando ambos campos están vacíos', async () => {
    const user = mostrarLogin();

    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('Por favor completa ambos campos.');

    expect(fetch).not.toHaveBeenCalled();
  });

  // PRUEBA 3
  test('muestra error cuando falta el correo', async () => {
    const user = mostrarLogin();

    await user.type(screen.getByLabelText('Contraseña'), '123456');
    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('Por favor completa ambos campos.');
  });

  // PRUEBA 4
  test('muestra error cuando falta la contraseña', async () => {
    const user = mostrarLogin();

    await user.type(screen.getByLabelText('Correo electrónico'), 'cliente@gmail.com');
    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('Por favor completa ambos campos.');
  });

  // PRUEBA 5
  test('rechaza correos de más de 100 caracteres', async () => {
    const user = mostrarLogin();

    await completarLogin(user, 'a'.repeat(101) + '@gmail.com', '123456');
    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('El correo no puede superar los 100 caracteres.');

    expect(fetch).not.toHaveBeenCalled();
  });

  // PRUEBA 6
  test('rechaza un dominio de correo no permitido', async () => {
    const user = mostrarLogin();

    await completarLogin(user, 'cliente@hotmail.com', '123456');
    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('El correo debe terminar en');

    expect(fetch).not.toHaveBeenCalled();
  });

  // PRUEBA 7
  test('rechaza un correo sin nombre antes del arroba', async () => {
    const user = mostrarLogin();

    await completarLogin(user, '@gmail.com', '123456');
    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('El correo debe terminar en');
  });

  // PRUEBA 8
  test('rechaza contraseñas con menos de 4 caracteres', async () => {
    const user = mostrarLogin();

    await completarLogin(user, 'cliente@gmail.com', '123');
    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('La contraseña debe tener entre 4 y 10 caracteres.');
  });

  // PRUEBA 9
  test('rechaza contraseñas con más de 10 caracteres', async () => {
    const user = mostrarLogin();

    await completarLogin(user, 'cliente@gmail.com', '12345678901');
    await enviarLogin(user);

    expect(screen.getByRole('alert').textContent)
      .toContain('La contraseña debe tener entre 4 y 10 caracteres.');
  });

  // PRUEBAS 10, 11, 12 y 13
  test.each([
    'duoc.cl',
    'duocuc.cl',
    'profesor.duoc.cl',
    'gmail.com'
  ])('permite iniciar sesión con dominio %s', async (dominio) => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user, `cliente@${dominio}`, '123456');
    await enviarLogin(user);

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledTimes(1);
      expect(screen.getByRole('alert').textContent)
        .toContain('¡Bienvenido! Iniciando sesión...');
    });
  });

  // PRUEBA 14
  test('envía correctamente el correo y la contraseña al backend', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user, 'cliente@gmail.com', '123456');
    await enviarLogin(user);

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith(
        expect.stringContaining('/auth/login'),
        expect.objectContaining({
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            correoUsuario: 'cliente@gmail.com',
            contrasenaUsuario: '123456'
          })
        })
      );
    });
  });

  // PRUEBA 15
  test('convierte el correo a minúsculas antes de enviarlo', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user, 'CLIENTE@GMAIL.COM', '123456');
    await enviarLogin(user);

    await waitFor(() => {
      const opciones = fetch.mock.calls[0][1];
      const datosEnviados = JSON.parse(opciones.body);

      expect(datosEnviados.correoUsuario).toBe('cliente@gmail.com');
    });
  });

  // PRUEBA 16
  test('elimina espacios alrededor del correo y contraseña', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user, '  cliente@gmail.com  ', '  123456  ');
    await enviarLogin(user);

    await waitFor(() => {
      const datosEnviados = JSON.parse(fetch.mock.calls[0][1].body);

      expect(datosEnviados.correoUsuario).toBe('cliente@gmail.com');
      expect(datosEnviados.contrasenaUsuario).toBe('123456');
    });
  });

  // PRUEBA 17
  test('guarda el token al iniciar sesión correctamente', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user);
    await enviarLogin(user);

    await waitFor(() => {
      expect(localStorage.getItem('token')).toBe('token-de-prueba');
    });
  });

  // PRUEBA 18
  test('guarda correctamente los datos del usuario activo', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user);
    await enviarLogin(user);

    await waitFor(() => {
      const usuario = JSON.parse(localStorage.getItem('usuarioActivo'));

      expect(usuario.id).toBe(1);
      expect(usuario.nombre).toBe('Cliente Prueba');
      expect(usuario.correo).toBe('cliente@gmail.com');
      expect(usuario.tipoUsuario).toBe('CLIENTE');
      expect(usuario.inicial).toBe('C');
    });
  });

  // PRUEBA 19
  test('muestra mensaje de bienvenida después de iniciar sesión', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user);
    await enviarLogin(user);

    expect(await screen.findByText('¡Bienvenido! Iniciando sesión...'))
      .toBeTruthy();
  });

  // PRUEBA 20
  test('emite el evento sesionActualizada', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    const escucharEvento = vi.fn();
    window.addEventListener('sesionActualizada', escucharEvento);

    try {
      await completarLogin(user);
      await enviarLogin(user);

      await waitFor(() => {
        expect(escucharEvento).toHaveBeenCalledTimes(1);
      });
    } finally {
      window.removeEventListener('sesionActualizada', escucharEvento);
    }
  });

  // PRUEBA 21
  test('muestra error cuando las credenciales son incorrectas', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(
      respuestaError('Correo o contraseña incorrectos.')
    );

    await completarLogin(user);
    await enviarLogin(user);

    expect(await screen.findByText('Correo o contraseña incorrectos.'))
      .toBeTruthy();

    expect(localStorage.getItem('token')).toBeNull();
  });

  // PRUEBA 22
  test('muestra un error del backend enviado como texto plano', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue({
      ok: false,
      status: 500,
      text: async () => 'Error interno del servidor'
    });

    await completarLogin(user);
    await enviarLogin(user);

    expect(await screen.findByText('Error interno del servidor'))
      .toBeTruthy();
  });

  // PRUEBA 23
  test('muestra mensaje por defecto si el backend no entrega un mensaje', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaError(''));

    await completarLogin(user);
    await enviarLogin(user);

    expect(await screen.findByText('Correo o contraseña incorrectos.'))
      .toBeTruthy();
  });

  // PRUEBA 24
  test('muestra error cuando no se puede conectar al servidor', async () => {
    const user = mostrarLogin();

    fetch.mockRejectedValue(new Error('Sin conexión'));

    await completarLogin(user);
    await enviarLogin(user);

    expect(await screen.findByText(
      'Error al conectar con el servidor. Revisa que el backend esté corriendo.'
    )).toBeTruthy();
  });

  // PRUEBA 25
  test('limpia el mensaje de error al modificar el correo', async () => {
    const user = mostrarLogin();

    await enviarLogin(user);

    expect(screen.getByRole('alert')).toBeTruthy();

    await user.type(screen.getByLabelText('Correo electrónico'), 'a');

    expect(screen.queryByRole('alert')).toBeNull();
  });

  // PRUEBA 26
  test('limpia el mensaje de error al modificar la contraseña', async () => {
    const user = mostrarLogin();

    await enviarLogin(user);

    expect(screen.getByRole('alert')).toBeTruthy();

    await user.type(screen.getByLabelText('Contraseña'), '1');

    expect(screen.queryByRole('alert')).toBeNull();
  });

  // PRUEBA 27
  test('deshabilita el botón mientras se procesa el Login', async () => {
    const user = mostrarLogin();

    // Dejamos pendiente la respuesta del backend.
    fetch.mockImplementation(() => new Promise(() => {}));

    await completarLogin(user);
    await enviarLogin(user);

    const boton = screen.getByRole('button', { name: 'Ingresando...' });

    expect(boton.disabled).toBe(true);
  });

  // PRUEBA 28
  test('redirige al inicio después de un Login exitoso', async () => {
    const user = mostrarLogin();

    fetch.mockResolvedValue(respuestaExitosa());

    await completarLogin(user);
    await enviarLogin(user);

    // Login.jsx realiza la redirección después de 800 ms.
    expect(await screen.findByText(
      'Página principal',
      {},
      { timeout: 2000 }
    )).toBeTruthy();
  });

});
