import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AdminVentanas from '../pages/admin/AdminVentanas';
import '@testing-library/jest-dom';

describe('AdminVentanas - Suite Completa', () => {
  test('debe renderizar el panel principal de ventanas y las opciones de accesos rápidos', async () => {
    render(<AdminVentanas />);

    expect(await screen.findByText(/gestión de ventanas y modales/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /configurar accesos rápidos/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /gestionar banners publicitarios/i })).toBeInTheDocument();
  });

  test('debe abrir la ventana modal correspondiente, permitir interactuar y cerrarla limpiando el estado', async () => {
    render(<AdminVentanas />);

    const btnAbrirVentana = screen.getByRole('button', { name: /configurar accesos rápidos/i });
    await userEvent.click(btnAbrirVentana);

    const modalTitle = await screen.findByText(/panel de accesos rápidos/i);
    expect(modalTitle).toBeInTheDocument();
    expect(screen.getByLabelText(/enlace rápido 1/i)).toBeInTheDocument();

    // Cierra la ventana mediante el botón de cierre
    const btnCerrar = screen.getByRole('button', { name: /cerrar ventana/i });
    await userEvent.click(btnCerrar);

    await waitFor(() => {
      expect(modalTitle).not.toBeInTheDocument();
    });
  });

  test('debe cerrar el modal al presionar la tecla Escape del teclado', async () => {
    render(<AdminVentanas />);

    const btnAbrirVentana = screen.getByRole('button', { name: /configurar accesos rápidos/i });
    await userEvent.click(btnAbrirVentana);

    const modalTitle = await screen.findByText(/panel de accesos rápidos/i);
    expect(modalTitle).toBeInTheDocument();

    await userEvent.keyboard('{Escape}');

    await waitFor(() => {
      expect(modalTitle).not.toBeInTheDocument();
    });
  });
});