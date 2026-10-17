import { cleanup, fireEvent, render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'

import Registro from './Registro.jsx'

// ========================================
// 1. DATOS Y CONFIGURACIÓN DEL MINIMARKET
// ========================================

const datosValidos = {
    run: '216830172',
    nombre: 'bairon',
    apellidos: 'valencia leiva',
    correo: 'bairon@gmail.com',
    tipoUsuario: 'Cliente',
    direccion: 'mena 1735',
    contrasena: '12345',
    confirmarContrasena: '12345',
    fechaNacimiento: '2004-10-08',
}

const etiquetas = {
    run: /^RUN/,
    nombre: /^Nombre/,
    apellidos: /^Apellidos/,
    correo: /^Correo electrónico/,
    tipoUsuario: /^Tipo de usuario/,
    direccion: /^Dirección/,
    contrasena: /^Contraseña/,
    confirmarContrasena: /^Confirmar contraseña/,
    fechaNacimiento: /^Fecha de nacimiento/,
}

function mostrar() {
    render(
        <MemoryRouter initialEntries={['/registro']}>
            <Routes>
                <Route path="/registro" element={<Registro />} />
                <Route path="/login" element={<h1>Destino: Iniciar Sesión</h1>} />
            </Routes>
        </MemoryRouter>
    )

    return {
        user: userEvent.setup(),
    }
}

// ========================================
// 2. INTERACCIONES DEL FORMULARIO
// ========================================

function completar(user, cambios = {}) {
    const datos = {
        ...datosValidos,
        ...cambios,
    }

    for (const [campo, valor] of Object.entries(datos)) {
        if (valor === undefined) continue
        const control = screen.getByLabelText(etiquetas[campo])
        fireEvent.change(control, { target: { name: control.name || campo, value: valor } })
    }

    const regionSelect = screen.getByLabelText(/^Región/)
    fireEvent.change(regionSelect, { target: { name: regionSelect.name || 'region', value: 'Región Metropolitana' } })

    const comunaSelect = screen.getByLabelText(/^Comuna/)
    fireEvent.change(comunaSelect, { target: { name: comunaSelect.name || 'comuna', value: 'Santiago' } })
}

async function enviar(user) {
    await user.click(screen.getByRole('button', { name: /Registrarme/i }))
}

beforeEach(() => {
    localStorage.clear()
})

afterEach(() => {
    cleanup()
    vi.restoreAllMocks()
})

// ========================================
// 3. PRUEBAS UNITARIAS (DESCRIBE PRINCIPAL)
// ========================================

describe('Pruebas unitarias para Registro de Minimarket', () => {

    test('muestra mensaje de error si se intenta enviar el formulario vacío', async () => {
        const { user } = mostrar()
        expect(screen.getByRole('heading', { name: /^Registro$/i })).toBeInTheDocument()
        await enviar(user)
        expect(await screen.findByText('Por favor completa todos los campos obligatorios.')).toBeInTheDocument()
    })

    // === LIMPIEZA DE ERRORES AL ESCRIBIR (CUBRE TODAS LAS LÍNEAS DE ONCHANGE) ===
    test.each([
        ['run', /^RUN/, '2'],
        ['nombre', /^Nombre/, 'b'],
        ['apellidos', /^Apellidos/, 'v'],
        ['correo', /^Correo electrónico/, 'b@g.com'],
        ['direccion', /^Dirección/, 'm'],
        ['contrasena', /^Contraseña/, '1'],
        ['confirmarContrasena', /^Confirmar contraseña/, '1'],
    ])('limpia el mensaje de error cuando Bairon modifica el campo %s', async (nombreCampo, etiqueta, valorTest) => {
        const { user } = mostrar()
        await enviar(user)
        expect(screen.getByText('Por favor completa todos los campos obligatorios.')).toBeInTheDocument()

        const control = screen.getByLabelText(etiqueta)
        fireEvent.change(control, { target: { name: control.name || nombreCampo, value: valorTest } })

        expect(screen.queryByText('Por favor completa todos los campos obligatorios.')).not.toBeInTheDocument()
    })

    // === LIMPIEZA DE ERRORES AL CAMBIAR REGIÓN O COMUNA (LÍNEAS 149-156) ===
    test('limpia el estado de error al cambiar la región o comuna', async () => {
        const { user } = mostrar()

        await enviar(user)
        expect(screen.getByText('Por favor completa todos los campos obligatorios.')).toBeInTheDocument()

        const regionSelect = screen.getByLabelText(/^Región/)
        fireEvent.change(regionSelect, { target: { name: regionSelect.name || 'region', value: 'Valparaíso' } })
        expect(screen.queryByText('Por favor completa todos los campos obligatorios.')).not.toBeInTheDocument()

        await enviar(user)
        expect(screen.getByText('Por favor completa todos los campos obligatorios.')).toBeInTheDocument()

        const comunaSelect = screen.getByLabelText(/^Comuna/)
        fireEvent.change(comunaSelect, { target: { name: comunaSelect.name || 'comuna', value: 'Quilpué' } })
        expect(screen.queryByText('Por favor completa todos los campos obligatorios.')).not.toBeInTheDocument()
    })

    // === VALIDACIONES DE LÍMITE Y FORMATO ===
    test.each([
        ['run', '12.345.678-5', 'El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion, y un dígito verificador válido.'],
        ['run', '12345678Z', 'El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion, y un dígito verificador válido.'],
        ['nombre', 'a'.repeat(51), 'El nombre no puede superar los 50 caracteres.'],
        ['apellidos', 'a'.repeat(101), 'Los apellidos no pueden superar los 100 caracteres.'],
        ['correo', 'bairon@hotmail.com', 'Ingresa un correo @duoc.cl, @profesor.duoc.cl o @gmail.com.'],
        ['correo', `${'a'.repeat(91)}@gmail.com`, 'El correo no puede superar los 100 caracteres.'],
        ['direccion', 'a'.repeat(301), 'La dirección no puede superar los 300 caracteres.'],
        ['contrasena', '123', 'La contraseña debe tener entre 4 y 10 caracteres.'],
        ['contrasena', '12345678901', 'La contraseña debe tener entre 4 y 10 caracteres.'],
        ['confirmarContrasena', 'claveDiferente', 'Las contraseñas no coinciden.'],
    ])('rechaza %s con valor inválido y muestra el mensaje correspondiente', async (campo, valor, mensajeEsperado) => {
        const { user } = mostrar()
        completar(user, { [campo]: valor })
        await enviar(user)

        expect(await screen.findByText(mensajeEsperado)).toBeInTheDocument()
        expect(screen.queryByText('Destino: Iniciar Sesión')).not.toBeInTheDocument()
    })

    // === COBERTURA DEL ALGORITMO MÓDULO 11 (RUN MATEMÁTICO) ===
    test.each([
        ['216830170'], // DV real es 2, el '0' lo rechaza
        ['14335340'],  // DV real es K, el '0' lo rechaza obligando a pasar por el IF de K
        ['1234561']    // DV real es 0, el '1' lo rechaza obligando a pasar por el IF de 0
    ])('ejecuta la validación matemática de Módulo 11 rechazando el RUN inválido %s', async (runInvalido) => {
        const { user } = mostrar()
        completar(user, { run: runInvalido })
        await enviar(user)

        expect(
            await screen.findByText('El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion, y un dígito verificador válido.')
        ).toBeInTheDocument()
    })

    // === LOGICA DE REGION Y COMUNA ===
    test('habilita comunas según región seleccionada y limpia comuna al cambiar región', async () => {
        mostrar()
        const regionSelect = screen.getByLabelText(/^Región/)
        const comunaSelect = screen.getByLabelText(/^Comuna/)

        expect(comunaSelect).toBeDisabled()

        fireEvent.change(regionSelect, { target: { name: regionSelect.name || 'region', value: 'Región Metropolitana' } })
        expect(comunaSelect).toBeEnabled()
        fireEvent.change(comunaSelect, { target: { name: comunaSelect.name || 'comuna', value: 'Santiago' } })

        fireEvent.change(regionSelect, { target: { name: regionSelect.name || 'region', value: 'Valparaíso' } })
        expect(comunaSelect).toHaveValue('')
        expect(within(comunaSelect).queryByRole('option', { name: 'Santiago' })).not.toBeInTheDocument()
        expect(within(comunaSelect).getByRole('option', { name: 'Viña del Mar' })).toBeInTheDocument()
    })

    test('deselecciona la región restableciendo y deshabilitando la comuna', async () => {
        mostrar()
        const regionSelect = screen.getByLabelText(/^Región/)
        const comunaSelect = screen.getByLabelText(/^Comuna/)

        fireEvent.change(regionSelect, { target: { name: regionSelect.name || 'region', value: 'Región Metropolitana' } })
        expect(comunaSelect).toBeEnabled()

        fireEvent.change(regionSelect, { target: { name: regionSelect.name || 'region', value: '' } })
        expect(comunaSelect).toBeDisabled()
        expect(comunaSelect).toHaveValue('')
    })

    // === REGISTROS EXITOSOS Y REDIRECCIÓN ===
    test.each(['duoc.cl', 'duocuc.cl', 'profesor.duoc.cl', 'gmail.com'])(
        'acepta el dominio @%s para el correo de Bairon',
        async (dominio) => {
            const { user } = mostrar()
            completar(user, { correo: `bairon@${dominio}` })
            await enviar(user)
            expect(await screen.findByText(/¡Registro completado con éxito!/i)).toBeInTheDocument()
        }
    )

    test('acepta RUN terminado en K y completa el registro exitosamente', async () => {
        const { user } = mostrar()
        completar(user, { run: '1433534K' }) // RUN válido que termina en K
        await enviar(user)
        expect(await screen.findByText(/¡Registro completado con éxito!/i)).toBeInTheDocument()
        expect(await screen.findByRole('heading', { name: 'Destino: Iniciar Sesión' }, { timeout: 2000 })).toBeInTheDocument()
    })

    test('registra exitosamente a Bairon, guarda sus datos en localStorage y redirige a /login', async () => {
        const { user } = mostrar()
        completar(user)
        await enviar(user)
        expect(await screen.findByText(/¡Registro completado con éxito!/i)).toBeInTheDocument()

        const usuarios = JSON.parse(localStorage.getItem('usuariosRegistrados') || '[]')
        expect(usuarios).toHaveLength(1)
        expect(usuarios[0]).toEqual(
            expect.objectContaining({
                run: '216830172',
                nombre: 'bairon',
                apellidos: 'valencia leiva',
                correo: 'bairon@gmail.com',
            })
        )

        expect(await screen.findByRole('heading', { name: 'Destino: Iniciar Sesión' }, { timeout: 2000 })).toBeInTheDocument()
    })

    test('no permite registrar si el RUN o Correo ya existen en localStorage', async () => {
        localStorage.setItem(
            'usuariosRegistrados',
            JSON.stringify([{ run: '216830172', correo: 'bairon@gmail.com' }])
        )
        const { user } = mostrar()
        completar(user)
        await enviar(user)
        expect(await screen.findByText('Ya existe un usuario registrado con ese RUN o correo electrónico.')).toBeInTheDocument()
    })

})