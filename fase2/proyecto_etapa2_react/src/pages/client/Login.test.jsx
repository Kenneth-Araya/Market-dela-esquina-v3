import { cleanup, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'

import Login from './Login.jsx'

// ========================================
// 1. DATOS Y CONFIGURACIÓN DE PRUEBA
// ========================================

const usuarioBairon = {
  nombre: 'bairon',
  correo: 'bairon@gmail.com',
  tipoUsuario: 'Cliente',
  contrasena: '12345',
}

const etiquetas = {
  correo: /^Correo electrónico/,
  contrasena: /^Contraseña/,
}

function mostrar() {
  render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<h1>Página Principal (Home)</h1>} />
        <Route path="/admin" element={<h1>Panel de Administración</h1>} />
      </Routes>
    </MemoryRouter>
  )

  return {
    user: userEvent.setup(),
  }
}

async function completar(user, { correo, contrasena }) {
  if (correo !== undefined) {
    const inputCorreo = screen.getByLabelText(etiquetas.correo)
    await user.click(inputCorreo)
    await user.clear(inputCorreo)
    if (correo) await user.paste(correo)
  }

  if (contrasena !== undefined) {
    const inputContrasena = screen.getByLabelText(etiquetas.contrasena)
    await user.click(inputContrasena)
    await user.clear(inputContrasena)
    if (contrasena) await user.paste(contrasena)
  }
}

async function enviar(user) {
  await user.click(
    screen.getByRole('button', {
      name: /^Iniciar$/i,
    })
  )
}

beforeEach(() => {
  localStorage.clear()
})

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
})

// ========================================
// 2. PRUEBAS DE RENDERIZADO Y VALIDACIÓN
// ========================================

describe('Pruebas unitarias para Login del Minimarket', () => {
  test('renderiza el formulario de login y muestra error al enviar campos vacíos', async () => {
    const { user } = mostrar()

    expect(
      screen.getByRole('heading', { name: /^Login$/i })
    ).toBeInTheDocument()

    await enviar(user)

    expect(
      await screen.findByText('Por favor completa ambos campos.')
    ).toBeInTheDocument()
  })

  test('limpia el mensaje de error cuando Bairon escribe en correo o contraseña', async () => {
    const { user } = mostrar()

    await enviar(user)
    expect(screen.getByText('Por favor completa ambos campos.')).toBeInTheDocument()

    await user.type(screen.getByLabelText(etiquetas.correo), 'b')
    expect(screen.queryByText('Por favor completa ambos campos.')).not.toBeInTheDocument()

    await enviar(user)
    expect(screen.getByText('Por favor completa ambos campos.')).toBeInTheDocument()

    await user.type(screen.getByLabelText(etiquetas.contrasena), '1')
    expect(screen.queryByText('Por favor completa ambos campos.')).not.toBeInTheDocument()
  })

  test.each([
    [
      `${'a'.repeat(92)}@gmail.com`,
      '12345',
      'El correo no puede superar los 100 caracteres.',
    ],
    [
      'bairon@hotmail.com',
      '12345',
      'El correo debe terminar en @duoc.cl, @profesor.duoc.cl o @gmail.com.',
    ],
    [
      'bairon@gmail.com',
      '123',
      'La contraseña debe tener entre 4 y 10 caracteres.',
    ],
    [
      'bairon@gmail.com',
      '12345678901',
      'La contraseña debe tener entre 4 y 10 caracteres.',
    ],
  ])(
    'rechaza login con formato inválido e informa el error correspondiente',
    async (correo, contrasena, mensajeEsperado) => {
      const { user } = mostrar()

      await completar(user, { correo, contrasena })
      await enviar(user)

      expect(await screen.findByText(mensajeEsperado)).toBeInTheDocument()
    }
  )

  // ========================================
  // 3. VERIFICACIÓN DE CREDENCIALES
  // ========================================

  test('muestra error si el correo de Bairon no está registrado en localStorage', async () => {
    const { user } = mostrar()

    await completar(user, {
      correo: 'bairon@gmail.com',
      contrasena: '12345',
    })
    await enviar(user)

    expect(
      await screen.findByText(
        'Este correo no está registrado. Por favor regístrate primero.'
      )
    ).toBeInTheDocument()
  })

  test('muestra error si la contraseña de Bairon es incorrecta', async () => {
    localStorage.setItem(
      'usuariosRegistrados',
      JSON.stringify([usuarioBairon])
    )

    const { user } = mostrar()

    await completar(user, {
      correo: 'bairon@gmail.com',
      contrasena: '99999', // 5 caracteres: cumple formato y gatilla la validación de credenciales
    })
    await enviar(user)

    expect(
      await screen.findByText('Contraseña incorrecta. Inténtalo nuevamente.')
    ).toBeInTheDocument()
  })

  // ========================================
  // 4. INICIO DE SESIÓN EXITOSO Y NAVEGACIÓN
  // ========================================

  test.each(['duoc.cl', 'duocuc.cl', 'profesor.duoc.cl', 'gmail.com'])(
    'permite iniciar sesión a Bairon con el dominio @%s',
    async (dominio) => {
      const correoValido = `bairon@${dominio}`
      localStorage.setItem(
        'usuariosRegistrados',
        JSON.stringify([{ ...usuarioBairon, correo: correoValido }])
      )

      const { user } = mostrar()

      await completar(user, {
        correo: correoValido,
        contrasena: '12345',
      })
      await enviar(user)

      expect(
        await screen.findByText('¡Bienvenido! Iniciando sesión...')
      ).toBeInTheDocument()
    }
  )

  test('inicia sesión correctamente, guarda datos en localStorage, dispara evento y redirige a /', async () => {
    localStorage.setItem(
      'usuariosRegistrados',
      JSON.stringify([usuarioBairon])
    )

    const eventoSesionSpy = vi.fn()
    window.addEventListener('sesionActualizada', eventoSesionSpy)

    const { user } = mostrar()

    await completar(user, {
      correo: 'BAIRON@GMAIL.COM',
      contrasena: '12345',
    })
    await enviar(user)

    expect(
      await screen.findByText('¡Bienvenido! Iniciando sesión...')
    ).toBeInTheDocument()

    const usuarioActivo = JSON.parse(
      localStorage.getItem('usuarioActivo') || '{}'
    )
    expect(usuarioActivo).toEqual({
      nombre: 'bairon',
      correo: 'bairon@gmail.com',
      tipoUsuario: 'Cliente',
      inicial: 'B',
      avatar: 'https://i.pravatar.cc/100?img=68',
    })

    expect(eventoSesionSpy).toHaveBeenCalledTimes(1)

    expect(
      await screen.findByRole(
        'heading',
        { name: 'Página Principal (Home)' },
        { timeout: 2000 }
      )
    ).toBeInTheDocument()

    window.removeEventListener('sesionActualizada', eventoSesionSpy)
  })
})