// @vitest-environment jsdom

import React from 'react';

import { cleanup, fireEvent, render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';

import Registro from './Registro.jsx';

// ========================================
// 1. DATOS DE PRUEBA
// ========================================

const datosValidos = {
    run: '216830172',
    nombre: 'Bairon',
    apellidos: 'Valencia Leiva',
    correo: 'bairon@gmail.com',
    fechaNacimiento: '2004-10-08',
    tipoUsuario: 'Cliente',
    direccion: 'Mena 1735',
    contrasena: '12345',
    confirmarContrasena: '12345'
};

// ========================================
// 2. CONFIGURACIÓN DE ETIQUETAS
// ========================================

const etiquetas = {
    run: /^RUN/,
    nombre: /^Nombre/,
    apellidos: /^Apellidos/,
    correo: /^Correo electrónico/,
    fechaNacimiento: /^Fecha de nacimiento/,
    tipoUsuario: /^Tipo de usuario/,
    direccion: /^Dirección/,
    contrasena: /^Contraseña$/,
    confirmarContrasena: /^Confirmar contraseña/
};

// ========================================
// 3. CONFIGURACIÓN DEL COMPONENTE
// ========================================

function mostrar() {
    const user = userEvent.setup();

    render(
        <MemoryRouter initialEntries={['/registro']}>
            <Routes>
                <Route path="/registro" element={<Registro />} />

                <Route
                    path="/login"
                    element={<h1>Destino: Iniciar Sesión</h1>}
                />
            </Routes>
        </MemoryRouter>
    );

    return { user };
}

// ========================================
// 4. COMPLETAR FORMULARIO
// ========================================

function completar(cambios = {}) {

    const datos = {
        ...datosValidos,
        ...cambios
    };

    // Completar los campos de texto
    for (const [campo, valor] of Object.entries(datos)) {

        if (valor === undefined) continue;

        const control = screen.getByLabelText(etiquetas[campo]);

        fireEvent.change(control, {
            target: { value: valor }
        });
    }

    // Seleccionar región
    const regionSelect = screen.getByLabelText(/^Región/);

    fireEvent.change(regionSelect, {
        target: { value: 'Región Metropolitana' }
    });

    // Seleccionar comuna
    const comunaSelect = screen.getByLabelText(/^Comuna/);

    fireEvent.change(comunaSelect, {
        target: { value: 'Santiago' }
    });
}

// ========================================
// 5. ENVIAR FORMULARIO
// ========================================

async function enviar(user) {

    await user.click(
        screen.getByRole('button', {
            name: /Registrarme/i
        })
    );
}

// ========================================
// 6. CONFIGURACIÓN DE PRUEBAS
// ========================================

beforeEach(() => {

    // Simular respuesta exitosa del backend
    vi.stubGlobal(
        'fetch',
        vi.fn().mockResolvedValue({
            ok: true,
            status: 200,
            statusText: 'OK',
            text: vi.fn().mockResolvedValue('')
        })
    );

});

afterEach(() => {

    cleanup();

    // Restaurar los mocks
    vi.unstubAllGlobals();
    vi.restoreAllMocks();

});

// ========================================
// 7. PRUEBAS DEL FORMULARIO REGISTRO
// ========================================

describe('Pruebas unitarias del Registro', () => {

    // ------------------------------------
    // PRUEBA 1: RENDERIZADO
    // ------------------------------------

    test('muestra correctamente el formulario de registro', () => {

        mostrar();

        expect(
            screen.getByRole('heading', { name: /^Registro$/i })
        ).toBeInTheDocument();

        expect(screen.getByLabelText(/^RUN/)).toBeInTheDocument();

        expect(screen.getByLabelText(/^Nombre/)).toBeInTheDocument();

        expect(screen.getByLabelText(/^Apellidos/)).toBeInTheDocument();

        expect(screen.getByLabelText(/^Correo electrónico/)).toBeInTheDocument();

        expect(
            screen.getByRole('button', { name: /Registrarme/i })
        ).toBeInTheDocument();

    });

    // ------------------------------------
    // PRUEBA 2: FORMULARIO VACÍO
    // ------------------------------------

    test('muestra error cuando se envía el formulario vacío', async () => {

        const { user } = mostrar();

        await enviar(user);

        expect(
            await screen.findByText(
                'Por favor completa todos los campos obligatorios.'
            )
        ).toBeInTheDocument();

        expect(fetch).not.toHaveBeenCalled();

    });

    // ------------------------------------
    // PRUEBA 3: LIMPIAR MENSAJES DE ERROR
    // ------------------------------------

    test.each([
        ['run', '2'],
        ['nombre', 'B'],
        ['apellidos', 'V'],
        ['correo', 'b@gmail.com'],
        ['direccion', 'M'],
        ['contrasena', '1'],
        ['confirmarContrasena', '1']
    ])(
        'limpia el mensaje de error al modificar el campo %s',
        async (campo, valor) => {

            const { user } = mostrar();

            await enviar(user);

            expect(
                screen.getByText(
                    'Por favor completa todos los campos obligatorios.'
                )
            ).toBeInTheDocument();

            const control = screen.getByLabelText(etiquetas[campo]);

            fireEvent.change(control, {
                target: { value: valor }
            });

            expect(
                screen.queryByText(
                    'Por favor completa todos los campos obligatorios.'
                )
            ).not.toBeInTheDocument();

        }
    );

    // ------------------------------------
    // PRUEBA 4: LIMPIAR ERROR AL CAMBIAR REGIÓN
    // ------------------------------------

    test('limpia el error al cambiar la región', async () => {

        const { user } = mostrar();

        await enviar(user);

        const regionSelect = screen.getByLabelText(/^Región/);

        fireEvent.change(regionSelect, {
            target: { value: 'Valparaíso' }
        });

        expect(
            screen.queryByText(
                'Por favor completa todos los campos obligatorios.'
            )
        ).not.toBeInTheDocument();

    });

    // ------------------------------------
    // PRUEBA 5: VALIDACIONES DE CAMPOS
    // ------------------------------------

    test.each([
        [
            'nombre',
            'a'.repeat(51),
            'El nombre no puede superar los 50 caracteres.'
        ],
        [
            'apellidos',
            'a'.repeat(101),
            'Los apellidos no pueden superar los 100 caracteres.'
        ],
        [
            'correo',
            'bairon@hotmail.com',
            'Ingresa un correo @duoc.cl, @profesor.duoc.cl o @gmail.com.'
        ],
        [
            'correo',
            'a'.repeat(91) + '@gmail.com',
            'El correo no puede superar los 100 caracteres.'
        ],
        [
            'direccion',
            'a'.repeat(301),
            'La dirección no puede superar los 300 caracteres.'
        ],
        [
            'contrasena',
            '123',
            'La contraseña debe tener entre 4 y 10 caracteres.'
        ],
        [
            'contrasena',
            '12345678901',
            'La contraseña debe tener entre 4 y 10 caracteres.'
        ],
        [
            'confirmarContrasena',
            'claveDiferente',
            'Las contraseñas no coinciden.'
        ]
    ])(
        'rechaza el campo %s cuando contiene un valor inválido',
        async (campo, valor, mensaje) => {

            const { user } = mostrar();

            completar({
                [campo]: valor
            });

            await enviar(user);

            expect(
                await screen.findByText(mensaje)
            ).toBeInTheDocument();

            expect(fetch).not.toHaveBeenCalled();

        }
    );

    // ------------------------------------
    // PRUEBA 6: REGIONES Y COMUNAS
    // ------------------------------------

    test('habilita las comunas cuando se selecciona una región', () => {

        mostrar();

        const regionSelect = screen.getByLabelText(/^Región/);

        const comunaSelect = screen.getByLabelText(/^Comuna/);

        // Inicialmente la comuna está deshabilitada
        expect(comunaSelect).toBeDisabled();

        // Seleccionar región Metropolitana
        fireEvent.change(regionSelect, {
            target: { value: 'Región Metropolitana' }
        });

        expect(comunaSelect).toBeEnabled();

        expect(
            within(comunaSelect).getByRole('option', {
                name: 'Santiago'
            })
        ).toBeInTheDocument();

        // Seleccionar comuna Santiago
        fireEvent.change(comunaSelect, {
            target: { value: 'Santiago' }
        });

        expect(comunaSelect).toHaveValue('Santiago');

        // Cambiar a Valparaíso
        fireEvent.change(regionSelect, {
            target: { value: 'Valparaíso' }
        });

        // La comuna anterior debe reiniciarse
        expect(comunaSelect).toHaveValue('');

        expect(
            within(comunaSelect).queryByRole('option', {
                name: 'Santiago'
            })
        ).not.toBeInTheDocument();

        expect(
            within(comunaSelect).getByRole('option', {
                name: 'Quilpué'
            })
        ).toBeInTheDocument();

    });

    // ------------------------------------
    // PRUEBA 7: COMUNA DESHABILITADA
    // ------------------------------------

    test('deshabilita la comuna cuando se reinicia la región', () => {

        mostrar();

        const regionSelect = screen.getByLabelText(/^Región/);

        const comunaSelect = screen.getByLabelText(/^Comuna/);

        fireEvent.change(regionSelect, {
            target: { value: 'Región Metropolitana' }
        });

        expect(comunaSelect).toBeEnabled();

        fireEvent.change(comunaSelect, {
            target: { value: 'Santiago' }
        });

        fireEvent.change(regionSelect, {
            target: { value: '' }
        });

        expect(comunaSelect).toBeDisabled();

        expect(comunaSelect).toHaveValue('');

    });

    // ------------------------------------
    // PRUEBA 8: DOMINIOS DE CORREO
    // ------------------------------------

    test.each([
        'duoc.cl',
        'duocuc.cl',
        'profesor.duoc.cl',
        'gmail.com'
    ])(
        'permite registrar un correo con dominio %s',
        async (dominio) => {

            const { user } = mostrar();

            completar({
                correo: `bairon@${dominio}`
            });

            await enviar(user);

            expect(
                await screen.findByText(
                    /¡Registro completado con éxito!/i
                )
            ).toBeInTheDocument();

        }
    );

    // ------------------------------------
    // PRUEBA 9: REGISTRO EXITOSO
    // ------------------------------------

    test('registra un usuario correctamente en el backend', async () => {

        const { user } = mostrar();

        completar();

        await enviar(user);

        // Comprobar mensaje de éxito
        expect(
            await screen.findByText(
                /¡Registro completado con éxito!/i
            )
        ).toBeInTheDocument();

        // Comprobar que se llamó al backend
        expect(fetch).toHaveBeenCalledTimes(1);

        // Obtener los datos enviados
        const [url, opciones] = fetch.mock.calls[0];

        expect(url).toContain('/usuarios');

        expect(opciones.method).toBe('POST');

        expect(opciones.headers).toEqual({
            'Content-Type': 'application/json'
        });

        // Comprobar información enviada
        const usuarioEnviado = JSON.parse(opciones.body);

        expect(usuarioEnviado).toEqual({
            runUsuario: '216830172',
            nombreUsuario: 'Bairon',
            apellidosUsuario: 'Valencia Leiva',
            correoUsuario: 'bairon@gmail.com',
            fechaNacimientoUsuario: '2004-10-08',
            tipoUsuario: 'CLIENTE',
            regionUsuario: 'Región Metropolitana',
            comunaUsuario: 'Santiago',
            direccionUsuario: 'Mena 1735',
            contrasenaUsuario: '12345',
            estadoUsuario: 'ACTIVO'
        });

    });

    // ------------------------------------
    // PRUEBA 10: FECHA DE NACIMIENTO OPCIONAL
    // ------------------------------------

    test('permite registrar un usuario sin fecha de nacimiento', async () => {

        const { user } = mostrar();

        completar({
            fechaNacimiento: ''
        });

        await enviar(user);

        expect(
            await screen.findByText(
                /¡Registro completado con éxito!/i
            )
        ).toBeInTheDocument();

        const opciones = fetch.mock.calls[0][1];

        const usuarioEnviado = JSON.parse(opciones.body);

        expect(usuarioEnviado.fechaNacimientoUsuario).toBeNull();

    });

    // ------------------------------------
    // PRUEBA 11: ERROR DEL SERVIDOR
    // ------------------------------------

    test('muestra un mensaje cuando el backend devuelve un error', async () => {

        fetch.mockResolvedValueOnce({
            ok: false,
            status: 400,
            statusText: 'Bad Request',
            text: vi.fn().mockResolvedValue('Error al registrar usuario')
        });

        const { user } = mostrar();

        completar();

        await enviar(user);

        expect(
            await screen.findByText(
                'Error del servidor: Error al registrar usuario'
            )
        ).toBeInTheDocument();

    });

    // ------------------------------------
    // PRUEBA 12: ERROR DE CONEXIÓN
    // ------------------------------------

    test('muestra un mensaje cuando no puede conectar con el backend', async () => {

        fetch.mockRejectedValueOnce(
            new Error('Error de conexión')
        );

        const { user } = mostrar();

        completar();

        await enviar(user);

        expect(
            await screen.findByText(
                'Error al conectar con el servidor. Revisa que el backend esté corriendo.'
            )
        ).toBeInTheDocument();

    });

    // ------------------------------------
    // PRUEBA 13: REDIRECCIÓN AL LOGIN
    // ------------------------------------

    test('redirige al login después de un registro exitoso', async () => {

        const { user } = mostrar();

        completar();

        await enviar(user);

        expect(
            await screen.findByText(
                /¡Registro completado con éxito!/i
            )
        ).toBeInTheDocument();

        expect(
            await screen.findByRole(
                'heading',
                { name: 'Destino: Iniciar Sesión' },
                { timeout: 3000 }
            )
        ).toBeInTheDocument();

    });

});
