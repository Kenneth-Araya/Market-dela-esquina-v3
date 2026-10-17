import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AdminConfiguracion from '../pages/admin/AdminConfiguracion';
import '@testing-library/jest-dom';

describe('AdminConfiguracion - Suite Completa', () => {
  test('debe renderizar todos los campos de configuración inicial de la tienda correctamente', async () => {
    render(<AdminConfiguracion />);
    
    expect(await screen.findByLabelText(/nombre de la tienda/i)).toHaveValue('Market De La Esquina');
    expect(screen.getByLabelText(/correo de soporte/i)).toHaveValue('soporte@delaesquina.cl');
    expect(screen.getByLabelText(/impuesto iva/i)).toHaveValue(19);
    expect(screen.getByLabelText(/modo mantenimiento/i)).not.toBeChecked();
  });

  test('debe mostrar errores de validación si se vacían campos obligatorios antes de guardar', async () => {
    render(<AdminConfiguracion />);
    
    const inputNombre = await screen.findByLabelText(/nombre de la tienda/i);
    await userEvent.clear(inputNombre);

    const btnGuardar = screen.getByRole('button', { name: /guardar cambios/i });
    await userEvent.click(btnGuardar);

    expect(await screen.findByText(/el nombre de la tienda es obligatorio/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /guardar cambios/i })).toBeDisabled();
  });

  test('debe permitir modificar parámetros y simular una respuesta exitosa del servidor', async () => {
    render(<AdminConfiguracion />);

    const inputNombre = await screen.findByLabelText(/nombre de la tienda/i);
    await userEvent.clear(inputNombre);
    await userEvent.type(inputNombre, 'Market Central Actualizado');

    const toggleMantenimiento = screen.getByLabelText(/modo mantenimiento/i);
    await userEvent.click(toggleMantenimiento);

    const btnGuardar = screen.getByRole('button', { name: /guardar cambios/i });
    await userEvent.click(btnGuardar);

    expect(await screen.findByText(/configuración actualizada correctamente/i)).toBeInTheDocument();
    expect(inputNombre).toHaveValue('Market Central Actualizado');
    expect(toggleMantenimiento).toBeChecked();
  });
});