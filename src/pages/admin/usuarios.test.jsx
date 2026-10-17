import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AdminUsuarios from '../pages/admin/AdminUsuarios';
import '@testing-library/jest-dom';

describe('AdminUsuarios - Suite Completa', () => {
  test('debe listar los usuarios registrados y permitir buscarlos mediante la barra de búsqueda', async () => {
    render(<AdminUsuarios />);

    expect(await screen.findByText('juan.perez@email.com')).toBeInTheDocument();
    expect(screen.getByText('maria.gomez@email.com')).toBeInTheDocument();

    const inputBusqueda = screen.getByPlaceholderText(/buscar por nombre o correo/i);
    await userEvent.type(inputBusqueda, 'juan');

    expect(screen.getByText('juan.perez@email.com')).toBeInTheDocument();
    expect(screen.queryByText('maria.gomez@email.com')).not.toBeInTheDocument();
  });

  test('debe permitir cambiar el rol de un usuario de cliente a administrador con confirmación', async () => {
    render(<AdminUsuarios />);

    const selectRol = await screen.findByTestId('select-rol-juan.perez@email.com');
    await userEvent.selectOptions(selectRol, 'admin');

    const btnGuardar = screen.getByRole('button', { name: /guardar cambios de usuario/i });
    await userEvent.click(btnGuardar);

    expect(await screen.findByText(/rol actualizado con éxito/i)).toBeInTheDocument();
    expect(selectRol).toHaveValue('admin');
  });

  test('debe permitir bloquear o eliminar a un usuario del sistema previa confirmación de modal', async () => {
    render(<AdminUsuarios />);

    const btnEliminar = await screen.findByTestId('btn-eliminar-maria.gomez@email.com');
    await userEvent.click(btnEliminar);

    expect(await screen.findByText(/¿estás seguro de eliminar este usuario?/i)).toBeInTheDocument();
    
    const btnConfirmarModal = screen.getByRole('button', { name: /sí, eliminar usuario/i });
    await userEvent.click(btnConfirmarModal);

    expect(await screen.findByText(/usuario eliminado correctamente/i)).toBeInTheDocument();
    expect(screen.queryByText('maria.gomez@email.com')).not.toBeInTheDocument();
  });
});