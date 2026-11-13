
 // @vitest-environment jsdom

import React from 'react';
import { render, screen, waitFor, cleanup, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, test, expect, beforeEach, afterEach, vi } from 'vitest';
import Productos from './Productos.jsx';

// Simulamos dos productos que normalmente vienen del backend.
const productosPrueba = [
  {
    id: 1,
    nombreProducto: 'Arroz',
    precioProducto: 1500,
    imagenProducto: 'arroz.jpg',
    pesoProducto: '1 kg',
    ratingProducto: 4.5
  },
  {
    id: 2,
    nombreProducto: 'Leche',
    precioProducto: 1200,
    imagenProducto: 'leche.jpg',
    pesoProducto: '1 litro',
    ratingProducto: 4.8
  }
];

// Simulamos una respuesta correcta de la API.
function respuestaExitosa(datos = productosPrueba) {
  return {
    ok: true,
    json: async () => datos
  };
}

// Renderizamos el componente y esperamos sus productos.
async function mostrarProductos() {
  const user = userEvent.setup();

  render(<Productos />);

  await screen.findByText('Arroz');

  return user;
}

// Buscamos la tarjeta de un producto por su nombre.
function tarjetaProducto(nombre) {
  const imagen = screen.getByAltText(nombre);
  const tarjeta = imagen.closest('.tarjeta-producto');

  if (!tarjeta) {
    throw new Error(`No se encontró la tarjeta de ${nombre}`);
  }

  return within(tarjeta);
}

// Consultamos el carrito guardado.
function obtenerCarrito() {
  return JSON.parse(localStorage.getItem('carritoMarket'));
}

describe('Pruebas unitarias de Productos', () => {

  beforeEach(() => {
    // Limpiamos los datos antes de cada prueba.
    localStorage.clear();

    // Simulamos fetch para no usar el backend real.
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(respuestaExitosa()));

    // Ocultamos errores esperados de pruebas negativas.
    vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    cleanup();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  // PRUEBA 1
  test('muestra correctamente el título de productos', async () => {
    await mostrarProductos();

    expect(screen.getByText('Todos Nuestros Productos')).toBeTruthy();
  });

  // PRUEBA 2
  test('consulta los productos desde el backend', async () => {
    await mostrarProductos();

    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch.mock.calls[0][0]).toContain('/productos');
  });

  // PRUEBA 3
  test('muestra correctamente los productos recibidos', async () => {
    await mostrarProductos();

    expect(screen.getByText('Arroz')).toBeTruthy();
    expect(screen.getByText('Leche')).toBeTruthy();
  });

  // PRUEBA 4
  test('muestra las imágenes correctas', async () => {
    await mostrarProductos();

    expect(screen.getByAltText('Arroz').getAttribute('src'))
      .toBe('/img/arroz.jpg');

    expect(screen.getByAltText('Leche').getAttribute('src'))
      .toBe('/img/leche.jpg');
  });

  // PRUEBA 5
  test('muestra los precios con formato chileno', async () => {
    await mostrarProductos();

    expect(tarjetaProducto('Arroz').getByText('$1.500')).toBeTruthy();
    expect(tarjetaProducto('Leche').getByText('$1.200')).toBeTruthy();
  });

  // PRUEBA 6
  test('muestra el peso y la calificación del producto', async () => {
    await mostrarProductos();

    const arroz = tarjetaProducto('Arroz');

    expect(arroz.getByText('1 kg')).toBeTruthy();
    expect(arroz.getByText('(4.5/5)')).toBeTruthy();
  });

  // PRUEBA 7
  test('muestra el botón para aumentar cantidad', async () => {
    await mostrarProductos();

    const arroz = tarjetaProducto('Arroz');

    expect(arroz.getByRole('button', { name: 'Aumentar cantidad' }))
      .toBeTruthy();
  });

  // PRUEBA 8
  test('agrega un producto al carrito', async () => {
    const user = await mostrarProductos();

    await user.click(
      tarjetaProducto('Arroz').getByRole('button', { name: 'Aumentar cantidad' })
    );

    await waitFor(() => {
      const carrito = obtenerCarrito();

      expect(carrito).toHaveLength(1);
      expect(carrito[0].id).toBe(1);
      expect(carrito[0].cantidad).toBe(1);
    });
  });

  // PRUEBA 9
  test('guarda los datos correctos del producto', async () => {
    const user = await mostrarProductos();

    await user.click(
      tarjetaProducto('Arroz').getByRole('button', { name: 'Aumentar cantidad' })
    );

    await waitFor(() => {
      expect(obtenerCarrito()[0]).toEqual({
        id: 1,
        nombre: 'Arroz',
        precio: 1500,
        img: 'arroz.jpg',
        cantidad: 1
      });
    });
  });

  // PRUEBA 10
  test('aumenta la cantidad de un producto existente', async () => {
    const user = await mostrarProductos();

    const boton = tarjetaProducto('Arroz')
      .getByRole('button', { name: 'Aumentar cantidad' });

    await user.click(boton);
    await user.click(boton);
    await user.click(boton);

    await waitFor(() => {
      const carrito = obtenerCarrito();

      expect(carrito).toHaveLength(1);
      expect(carrito[0].cantidad).toBe(3);
    });
  });

  // PRUEBA 11
  test('muestra la cantidad actual del producto', async () => {
    const user = await mostrarProductos();

    await user.click(
      tarjetaProducto('Arroz').getByRole('button', { name: 'Aumentar cantidad' })
    );

    expect(tarjetaProducto('Arroz').getByText('1')).toBeTruthy();
  });

  // PRUEBA 12
  test('disminuye la cantidad de un producto', async () => {
    const user = await mostrarProductos();

    const arroz = tarjetaProducto('Arroz');

    await user.click(
      arroz.getByRole('button', { name: 'Aumentar cantidad' })
    );

    await user.click(
      arroz.getByRole('button', { name: 'Aumentar cantidad' })
    );

    await user.click(
      arroz.getByRole('button', { name: 'Disminuir cantidad' })
    );

    await waitFor(() => {
      expect(obtenerCarrito()[0].cantidad).toBe(1);
    });
  });

  // PRUEBA 13
  test('elimina el producto cuando su cantidad llega a cero', async () => {
    const user = await mostrarProductos();

    const arroz = tarjetaProducto('Arroz');

    await user.click(
      arroz.getByRole('button', { name: 'Aumentar cantidad' })
    );

    await user.click(
      arroz.getByRole('button', { name: 'Disminuir cantidad' })
    );

    await waitFor(() => {
      expect(obtenerCarrito()).toHaveLength(0);
    });
  });

  // PRUEBA 14
  test('permite agregar diferentes productos al carrito', async () => {
    const user = await mostrarProductos();

    await user.click(
      tarjetaProducto('Arroz').getByRole('button', { name: 'Aumentar cantidad' })
    );

    await user.click(
      tarjetaProducto('Leche').getByRole('button', { name: 'Aumentar cantidad' })
    );

    await waitFor(() => {
      const carrito = obtenerCarrito();

      expect(carrito).toHaveLength(2);
      expect(carrito[0].nombre).toBe('Arroz');
      expect(carrito[1].nombre).toBe('Leche');
    });
  });

  // PRUEBA 15
  test('recupera el carrito guardado en localStorage', async () => {
    localStorage.setItem('carritoMarket', JSON.stringify([
      {
        id: 1,
        nombre: 'Arroz',
        precio: 1500,
        img: 'arroz.jpg',
        cantidad: 2
      }
    ]));

    await mostrarProductos();

    expect(tarjetaProducto('Arroz').getByText('2')).toBeTruthy();
    expect(obtenerCarrito()[0].cantidad).toBe(2);
  });

  // PRUEBA 16
  test('emite el evento carritoActualizado al agregar', async () => {
    const escucharEvento = vi.fn();
    window.addEventListener('carritoActualizado', escucharEvento);

    try {
      const user = await mostrarProductos();

      // El componente ya emitió un evento al montarse.
      escucharEvento.mockClear();

      await user.click(
        tarjetaProducto('Arroz').getByRole('button', { name: 'Aumentar cantidad' })
      );

      await waitFor(() => {
        expect(escucharEvento).toHaveBeenCalledTimes(1);
      });
    } finally {
      window.removeEventListener('carritoActualizado', escucharEvento);
    }
  });

  // PRUEBA 17
  test('maneja un error cuando el backend responde incorrectamente', async () => {
    fetch.mockResolvedValue({
      ok: false,
      status: 500
    });

    render(<Productos />);

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith(
        'Error al obtener los productos del servidor'
      );
    });

    expect(screen.getByText('Todos Nuestros Productos')).toBeTruthy();
    expect(screen.queryByText('Arroz')).toBeNull();
  });

  // PRUEBA 18
  test('maneja errores de conexión con el backend', async () => {
    fetch.mockRejectedValue(new Error('Sin conexión'));

    render(<Productos />);

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith(
        'Error conectando con el backend:',
        expect.any(Error)
      );
    });

    expect(screen.getByText('Todos Nuestros Productos')).toBeTruthy();
    expect(screen.queryByText('Arroz')).toBeNull();
  });

});
