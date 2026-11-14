// @vitest-environment jsdom
import React from 'react';
import { render, screen, cleanup, waitFor } from '@testing-library/react';
import { describe, test, expect, beforeEach, afterEach, vi } from 'vitest';
import VentasAdmin from './VentasAdmin.jsx';

const ventasMock = [
  {
    venta: { id: 1, fechaVenta: '2026-11-10T10:30:00', totalVenta: 3000, estadoVenta: 'PAGADA' },
    detalles: [{ nombreProducto: 'Arroz', cantidadDetalle: 2 }]
  },
  {
    venta: { id: 2, fechaVenta: '2026-11-12T15:45:00', totalVenta: 5000, estadoVenta: 'ANULADA' },
    detalles: [
      { nombreProducto: 'Leche', cantidadDetalle: 1 },
      { nombreProducto: 'Pan', cantidadDetalle: 2 }
    ]
  }
];

const estadisticasMock = {
  ventasTotales: 2,
  ingresosTotales: 8000,
  ticketPromedio: 4000,
  ultimaVenta: '2026-11-12T15:45:00'
};

describe('Pruebas de integración de VentasAdmin', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn());
    vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    cleanup();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  test('carga historial de ventas y estadísticas desde el backend ordenadas descendentemente', async () => {
    fetch
      .mockResolvedValueOnce({ ok: true, json: async () => ventasMock })
      .mockResolvedValueOnce({ ok: true, json: async () => estadisticasMock });

    render(<VentasAdmin />);

    expect(await screen.findByText('Arroz x2')).toBeInTheDocument();
    expect(screen.getByText('Leche x1, Pan x2')).toBeInTheDocument();
    expect(screen.getByText('PAGADA')).toBeInTheDocument();
    expect(screen.getByText('ANULADA')).toBeInTheDocument();

    expect(fetch).toHaveBeenCalledTimes(2);
    expect(fetch.mock.calls[0][0]).toContain('/ventas');
    expect(fetch.mock.calls[1][0]).toContain('/ventas/estadisticas');

    expect(screen.getByText('$8.000')).toBeInTheDocument();
    expect(screen.getByText('$4.000')).toBeInTheDocument();
  });

  test('muestra guion en última venta y mensaje vacío cuando no hay registros', async () => {
    fetch
      .mockResolvedValueOnce({ ok: true, json: async () => [] })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ ventasTotales: 0, ingresosTotales: 0, ticketPromedio: 0, ultimaVenta: null })
      });

    render(<VentasAdmin />);

    expect(await screen.findByText('No hay ventas registradas.')).toBeInTheDocument();
    expect(screen.getByText('-')).toBeInTheDocument();
  });

  test('maneja errores HTTP de los endpoints de ventas y estadísticas', async () => {
    fetch
      .mockResolvedValueOnce({ ok: false, status: 500 })
      .mockResolvedValueOnce({ ok: false, status: 500 });

    render(<VentasAdmin />);

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('Error al obtener las ventas del servidor');
      expect(console.error).toHaveBeenCalledWith('Error al obtener las estadísticas de ventas del servidor');
    });
  });

  test('maneja error de red al consultar ventas', async () => {
    fetch.mockRejectedValueOnce(new Error('Sin conexión'));

    render(<VentasAdmin />);

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('Error conectando con el backend:', expect.any(Error));
    });
  });
});