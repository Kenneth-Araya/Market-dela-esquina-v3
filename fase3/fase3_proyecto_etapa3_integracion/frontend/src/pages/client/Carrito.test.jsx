
 // @vitest-environment jsdom

import React from 'react';
import '@testing-library/jest-dom/vitest';

import {
  cleanup,
  render,
  screen,
  waitFor
} from '@testing-library/react';

import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';

import {
  afterEach,
  beforeEach,
  describe,
  expect,
  test,
  vi
} from 'vitest';

import Carrito from './Carrito.jsx';

// ========================================
// 1. PRODUCTOS SIMULADOS PARA LAS PRUEBAS
// ========================================

const mockProductos = [
  {
    id: 1,
    nombre: 'Pan amasado',
    precio: '$1.500',
    cantidad: 2,
    img: 'pan.jpg'
  },
  {
    id: 2,
    nombre: 'Leche entera',
    precio: 1000,
    cantidad: 1,
    img: 'leche.jpg'
  },
  {
    id: 3,
    nombre: 'Regalo promocional',
    precio: 'Gratis',
    cantidad: 1,
    img: 'regalo.jpg'
  }
];

// ========================================
// 2. FUNCIONES DE APOYO
// ========================================

// Muestra el carrito dentro de React Router.
function mostrar() {
  const user = userEvent.setup();

  render(
    <MemoryRouter>
      <Carrito />
    </MemoryRouter>
  );

  return { user };
}

// Lee los productos guardados en localStorage.
function obtenerCarrito() {
  return JSON.parse(localStorage.getItem('carritoMarket'));
}

// Simula una respuesta exitosa del backend.
function respuestaExitosa() {
  return {
    ok: true,
    status: 200,
    json: async () => ({}),
    text: async () => ''
  };
}

// ========================================
// 3. CONFIGURACIÓN ANTES Y DESPUÉS
// ========================================

beforeEach(() => {

  // Comenzamos cada prueba sin información previa.
  localStorage.clear();

  // Simulamos las alertas del navegador.
  vi.spyOn(window, 'alert').mockImplementation(() => {});

  // Evitamos mostrar errores simulados en consola.
  vi.spyOn(console, 'error').mockImplementation(() => {});

});

afterEach(() => {

  // Desmontamos los componentes.
  cleanup();

  // Restauramos fetch y otras funciones globales.
  vi.unstubAllGlobals();

  // Restauramos los mocks.
  vi.restoreAllMocks();

  // Limpiamos localStorage.
  localStorage.clear();

});

// ========================================
// 4. PRUEBAS UNITARIAS DEL CARRITO
// ========================================

describe('Pruebas unitarias del Carrito de Compras', () => {

  // ----------------------------------------
  // PRUEBA 1: CARRITO VACÍO
  // ----------------------------------------

  test('muestra correctamente el carrito vacío', () => {

    mostrar();

    expect(
      screen.getByText('Tu Carrito de Compras')
    ).toBeInTheDocument();

    expect(
      screen.getByText('Tu carrito está vacío')
    ).toBeInTheDocument();

    // Deben mostrarse los montos de subtotal y total.
    const montosCero = screen.getAllByText('$0');

    expect(montosCero).toHaveLength(2);

  });

  // ----------------------------------------
  // PRUEBA 2: FINALIZAR SIN PRODUCTOS
  // ----------------------------------------

  test('muestra una alerta al intentar comprar con carrito vacío', async () => {

    const { user } = mostrar();

    // Intentamos finalizar una venta sin productos.
    await user.click(
      screen.getByRole('button', {
        name: /Finalizar Venta/i
      })
    );

    expect(window.alert).toHaveBeenCalledWith(
      'Tu carrito está vacío. Agrega productos antes de finalizar la venta.'
    );

    // Intentamos vaciar un carrito que ya está vacío.
    await user.click(
      screen.getByRole('button', {
        name: /Vaciar Carrito/i
      })
    );

    expect(
      screen.getByText('Tu carrito está vacío')
    ).toBeInTheDocument();

    expect(obtenerCarrito()).toEqual([]);

  });

  // ----------------------------------------
  // PRUEBA 3: MOSTRAR PRODUCTOS Y TOTAL
  // ----------------------------------------

  test('muestra productos y calcula correctamente el total', () => {

    // Guardamos productos de prueba.
    localStorage.setItem(
      'carritoMarket',
      JSON.stringify(mockProductos)
    );

    mostrar();

    // Comprobamos que aparezcan los productos.
    expect(
      screen.getByText('Pan amasado')
    ).toBeInTheDocument();

    expect(
      screen.getByText('Leche entera')
    ).toBeInTheDocument();

    expect(
      screen.getByText('Regalo promocional')
    ).toBeInTheDocument();

    // Pan: 1500 x 2 = 3000
    // Leche: 1000 x 1 = 1000
    // Regalo: 0
    // Total esperado: 4000

    const montos = screen.getAllByText('$4.000');

    expect(montos.length).toBeGreaterThan(0);

  });

  // ----------------------------------------
  // PRUEBA 4: AUMENTAR CANTIDAD
  // ----------------------------------------

  test('aumenta la cantidad y actualiza localStorage', async () => {

    // Agregamos una leche con cantidad 1.
    localStorage.setItem(
      'carritoMarket',
      JSON.stringify([mockProductos[1]])
    );

    const eventoSpy = vi.fn();

    window.addEventListener(
      'carritoActualizado',
      eventoSpy
    );

    try {

      const { user } = mostrar();

      // Aumentamos una unidad.
      await user.click(
        screen.getByLabelText('Sumar una unidad')
      );

      // La cantidad ahora debe ser 2.
      expect(
        screen.getByText('2')
      ).toBeInTheDocument();

      // El total debe cambiar a $2000.
      expect(
        screen.getAllByText('$2.000').length
      ).toBeGreaterThan(0);

      // Verificamos el almacenamiento.
      await waitFor(() => {
        expect(obtenerCarrito()[0].cantidad).toBe(2);
      });

      // Verificamos que se avise al Navbar.
      expect(eventoSpy).toHaveBeenCalled();

    } finally {

      window.removeEventListener(
        'carritoActualizado',
        eventoSpy
      );

    }

  });

  // ----------------------------------------
  // PRUEBA 5: DISMINUIR CANTIDAD
  // ----------------------------------------

  test('disminuye cantidades y elimina productos al llegar a cero', async () => {

    localStorage.setItem(
      'carritoMarket',
      JSON.stringify([
        mockProductos[0],
        mockProductos[1]
      ])
    );

    const { user } = mostrar();

    // Buscamos los botones para restar unidades.
    const botonesRestar = screen.getAllByLabelText(
      'Restar una unidad'
    );

    // Restamos una unidad al pan.
    await user.click(botonesRestar[0]);

    await waitFor(() => {
      expect(obtenerCarrito()[0].cantidad).toBe(1);
    });

    expect(
      screen.getByText('Pan amasado')
    ).toBeInTheDocument();

    // Restamos una unidad a la leche.
    // Como tenía una unidad, debe desaparecer.
    await user.click(
      screen.getAllByLabelText('Restar una unidad')[1]
    );

    await waitFor(() => {
      expect(obtenerCarrito()).toHaveLength(1);
    });

    expect(
      screen.queryByText('Leche entera')
    ).not.toBeInTheDocument();

  });

  // ----------------------------------------
  // PRUEBA 6: QUITAR PRODUCTO COMPLETO
  // ----------------------------------------

  test('elimina un producto con el botón Quitar todo', async () => {

    localStorage.setItem(
      'carritoMarket',
      JSON.stringify(mockProductos)
    );

    const { user } = mostrar();

    const botonesQuitar = screen.getAllByRole(
      'button',
      { name: /Quitar todo/i }
    );

    // Eliminamos el primer producto.
    await user.click(botonesQuitar[0]);

    expect(
      screen.queryByText('Pan amasado')
    ).not.toBeInTheDocument();

    await waitFor(() => {
      const carrito = obtenerCarrito();

      expect(carrito).toHaveLength(2);

      expect(
        carrito.some((producto) => producto.id === 1)
      ).toBe(false);
    });

  });

  // ----------------------------------------
  // PRUEBA 7: VACIAR CARRITO COMPLETO
  // ----------------------------------------

  test('vacía completamente el carrito', async () => {

    localStorage.setItem(
      'carritoMarket',
      JSON.stringify(mockProductos)
    );

    const { user } = mostrar();

    await user.click(
      screen.getByRole('button', {
        name: /Vaciar Carrito/i
      })
    );

    expect(
      screen.getByText('Tu carrito está vacío')
    ).toBeInTheDocument();

    await waitFor(() => {
      expect(obtenerCarrito()).toEqual([]);
    });

  });

  // ----------------------------------------
  // PRUEBA 8: COMPRA SIN SESIÓN
  // ----------------------------------------

  test('impide finalizar compra sin iniciar sesión', async () => {

    // Simulamos el backend.
    const fetchMock = vi.fn();

    vi.stubGlobal('fetch', fetchMock);

    // Agregamos productos, pero no usuario.
    localStorage.setItem(
      'carritoMarket',
      JSON.stringify(mockProductos)
    );

    const { user } = mostrar();

    await user.click(
      screen.getByRole('button', {
        name: /Finalizar Venta/i
      })
    );

    expect(window.alert).toHaveBeenCalledWith(
      'Debes iniciar sesión para finalizar la compra.'
    );

    // No debe consultar el backend.
    expect(fetchMock).not.toHaveBeenCalled();

    // Los productos deben mantenerse.
    expect(
      screen.getByText('Pan amasado')
    ).toBeInTheDocument();

    expect(obtenerCarrito()).toHaveLength(3);

  });

  // ----------------------------------------
  // PRUEBA 9: COMPRA EXITOSA
  // ----------------------------------------

  test('finaliza la compra y vacía el carrito', async () => {

    const fetchMock = vi.fn()
      .mockResolvedValue(respuestaExitosa());

    vi.stubGlobal('fetch', fetchMock);

    // Simulamos un usuario con sesión iniciada.
    localStorage.setItem(
      'usuarioActivo',
      JSON.stringify({
        id: 7,
        nombre: 'Cliente Prueba'
      })
    );

    // Guardamos productos.
    localStorage.setItem(
      'carritoMarket',
      JSON.stringify(mockProductos)
    );

    const { user } = mostrar();

    await user.click(
      screen.getByRole('button', {
        name: /Finalizar Venta/i
      })
    );

    // Esperamos la respuesta exitosa.
    expect(
      await screen.findByText('Tu carrito está vacío')
    ).toBeInTheDocument();

    // Comprobamos el mensaje de éxito.
    expect(window.alert).toHaveBeenCalledWith(
      '¡Compra realizada con éxito! Gracias por preferir Market De La Esquina.'
    );

    // Verificamos que el carrito quedó vacío.
    await waitFor(() => {
      expect(obtenerCarrito()).toEqual([]);
    });

    // Verificamos el endpoint de checkout.
    const urls = fetchMock.mock.calls.map(
      (llamada) => llamada[0]
    );

    expect(
      urls.some((url) =>
        url.endsWith('/ventas/checkout/7')
      )
    ).toBe(true);

  });

  // ----------------------------------------
  // PRUEBA 10: ERROR DEL BACKEND
  // ----------------------------------------

  test('conserva el carrito si el backend rechaza la compra', async () => {

    // Simulamos un backend que devuelve error de stock.
    const fetchMock = vi.fn((url) => {

      if (String(url).includes('/ventas/checkout/')) {
        return Promise.resolve({
          ok: false,
          status: 409,
          text: async () => JSON.stringify({
            mensaje: 'Stock insuficiente.'
          })
        });
      }

      return Promise.resolve(respuestaExitosa());

    });

    vi.stubGlobal('fetch', fetchMock);

    // Simulamos sesión iniciada.
    localStorage.setItem(
      'usuarioActivo',
      JSON.stringify({
        id: 7,
        nombre: 'Cliente Prueba'
      })
    );

    // Agregamos productos al carrito.
    localStorage.setItem(
      'carritoMarket',
      JSON.stringify(mockProductos)
    );

    const { user } = mostrar();

    // Intentamos comprar.
    await user.click(
      screen.getByRole('button', {
        name: /Finalizar Venta/i
      })
    );

    // El backend responde con stock insuficiente.
    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith(
        'Stock insuficiente.'
      );
    });

    // Los productos deben seguir disponibles.
    expect(
      screen.getByText('Pan amasado')
    ).toBeInTheDocument();

    // Comprobamos que el carrito no cambió.
    expect(obtenerCarrito()).toHaveLength(3);

  });

});
