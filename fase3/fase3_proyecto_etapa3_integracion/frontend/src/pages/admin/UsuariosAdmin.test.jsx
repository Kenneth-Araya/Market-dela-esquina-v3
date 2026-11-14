// @vitest-environment jsdom
import React from 'react';
import { render, screen, cleanup, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, test, expect, beforeEach, afterEach, vi } from 'vitest';
import UsuariosAdmin from './UsuariosAdmin.jsx';

const usuariosMock = [
  {
    id: 1,
    nombreUsuario: 'Ana',
    apellidosUsuario: 'Pérez',
    runUsuario: '111111111',
    correoUsuario: 'ana@duoc.cl',
    tipoUsuario: 'ADMINISTRADOR',
    regionUsuario: 'Región Metropolitana',
    comunaUsuario: 'Santiago',
    estadoUsuario: 'ACTIVO'
  },
  {
    id: 2,
    nombreUsuario: 'Luis',
    apellidosUsuario: 'Soto',
    runUsuario: '222222222',
    correoUsuario: 'luis@gmail.com',
    tipoUsuario: 'CLIENTE',
    regionUsuario: 'Valparaíso',
    comunaUsuario: 'Quilpué',
    estadoUsuario: 'INACTIVO'
  },
  {
    id: 3,
    nombreUsuario: 'Carla',
    apellidosUsuario: 'Díaz',
    runUsuario: '333333333',
    correoUsuario: 'carla@duocuc.cl',
    tipoUsuario: 'VENDEDOR',
    regionUsuario: 'Región Metropolitana',
    comunaUsuario: 'Maipú',
    estadoUsuario: 'ACTIVO'
  }
];

function renderUsuariosAdmin() {
  render(
    <MemoryRouter>
      <UsuariosAdmin />
    </MemoryRouter>
  );
}

describe('Pruebas de integración de UsuariosAdmin', () => {
  beforeEach(() => {
    localStorage.clear();
    localStorage.setItem('token', 'jwt-admin-123');
    vi.stubGlobal('fetch', vi.fn());
    vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    cleanup();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  test('envía token JWT en Authorization y renderiza usuarios y métricas por rol', async () => {
    fetch.mockResolvedValueOnce({ ok: true, json: async () => usuariosMock });

    renderUsuariosAdmin();

    expect(await screen.findByText('Ana Pérez')).toBeInTheDocument();
    expect(screen.getByText('Luis Soto')).toBeInTheDocument();
    expect(screen.getByText('Carla Díaz')).toBeInTheDocument();

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/usuarios'),
      expect.objectContaining({
        headers: { Authorization: 'Bearer jwt-admin-123' }
      })
    );

    expect(screen.getByText('ADMINISTRADOR')).toBeInTheDocument();
    expect(screen.getByText('CLIENTE')).toBeInTheDocument();
    expect(screen.getByText('VENDEDOR')).toBeInTheDocument();
    expect(screen.getByText('INACTIVO')).toBeInTheDocument();
  });

  test('registra error de sesión inválida ante respuesta 401', async () => {
    fetch.mockResolvedValueOnce({ ok: false, status: 401 });

    renderUsuariosAdmin();

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('Tu sesión no es válida o ha expirado');
    });
    expect(screen.getByText('No hay usuarios registrados.')).toBeInTheDocument();
  });

  test('registra error de permisos ante respuesta 403', async () => {
    fetch.mockResolvedValueOnce({ ok: false, status: 403 });

    renderUsuariosAdmin();

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('No tienes permisos para consultar usuarios');
    });
  });

  test('registra error genérico ante respuesta 500 y error de red', async () => {
    fetch.mockResolvedValueOnce({ ok: false, status: 500 });
    renderUsuariosAdmin();

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('Error al obtener los usuarios del servidor');
    });

    cleanup();
    fetch.mockRejectedValueOnce(new Error('Network down'));
    renderUsuariosAdmin();

    await waitFor(() => {
      expect(console.error).toHaveBeenCalledWith('Error conectando con el backend:', expect.any(Error));
    });
  });
});