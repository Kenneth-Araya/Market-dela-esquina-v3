// @vitest-environment jsdom
import React from 'react';
import { render, screen, cleanup, waitFor } from '@testing-library/react';
import { describe, test, expect, beforeEach, afterEach, vi } from 'vitest';
import ProductosAdmin from './ProductosAdmin.jsx';

const productosMock = [
  { id: 1, nombreProducto: 'Arroz Grado 1', pesoProducto: '1 kg', precioProducto: 1500 },
  { id: 2, nombreProducto: 'Leche Entera', pesoProducto: '1 L', precioProducto: 1200 },
  { id: 3, nombreProducto: 'Pan Amasado', pesoProducto: '500 g', precioProducto: 2000 }
];

const inventarioMock = [
  { idProducto: 1, stockActual: 10 },
  { idProducto: 2, stockActual: 5 }
  // Producto 3 sin registro en inventario -> stock 0
];

describe('Pruebas de integración de ProductosAdmin', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn());
    vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    cleanup();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  test('carga productos e inventario desde el backend y calcula estadísticas y subtotales', async () => {
    fetch
      .mockResolvedValueOnce({ ok: true, json: async () => productosMock })
      .mockResolvedValueOnce({ ok: true, json: async () => inventarioMock });

    render(<ProductosAdmin />);

    expect(await screen.findByText('Arroz Grado 1')).toBeInTheDocument();
    expect(screen.getByText('Leche Entera')).toBeInTheDocument();
    expect(screen.getByText('Pan Amasado')).toBeInTheDocument();

    expect(fetch).toHaveBeenCalledTimes(2);
    expect(fetch.mock.calls[0][0]).toContain('/productos');
    expect(fetch.mock.calls[1][0]).toContain('/inventario');

    // Unidades en stock: 10 + 5 + 0 = 15
    expect(screen.getByText('15')).toBeInTheDocument();
    // Valor total inventario: (1500*10) + (1200*5) = 21.000
    expect(screen.getByText('$21.000')).toBeInTheDocument();
  });

  test('muestra estado vacío cuando los endpoints devuelven listas vacías', async () => {
    fetch
      .mockResolvedValueOnce({ ok: true, json: async () => [] })
      .mockResolvedValueOnce({ ok: true, json: async () => [] });

    render(<ProductosAdmin />);

    expect(await screen.findByText('No hay productos en el catálogo.')).toBeInTheDocument();
    expect(screen.getByText('$0')).toBeInTheDocument();
  });

  test('maneja errores HTTP del servidor sin romper la vista', async () => {
    fetch
      .mockResolvedValueOnce({ ok: false, status: 500 })
      .mockResolvedValueOnce({ ok: false, status: 500 });

    render(<ProductosAdmin />);

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('Error al obtener los productos del servidor');
      expect(console.error).toHaveBeenCalledWith('Error al obtener el inventario del servidor');
    });
    expect(screen.getByText('No hay productos en el catálogo.')).toBeInTheDocument();
  });

  test('maneja error de conexión con el backend', async () => {
    fetch.mockRejectedValueOnce(new Error('Fallo de red'));

    render(<ProductosAdmin />);

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('Error conectando con el backend:', expect.any(Error));
    });
  });
});