import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AdminProducto from '../pages/admin/AdminProducto';
import '@testing-library/jest-dom';

describe('AdminProducto - Suite Completa', () => {
  test('debe listar los productos existentes en la tabla del administrador con todos sus atributos', async () => {
    render(<AdminProducto />);

    expect(await screen.findByText('Manzanas Fuji')).toBeInTheDocument();
    expect(screen.getByText('$1.500')).toBeInTheDocument();
    expect(screen.getByText('30 unidades')).toBeInTheDocument();
    expect(screen.getByText('Verduras')).toBeInTheDocument();
  });

  test('debe validar que no se pueda registrar un producto con precio o stock negativo', async () => {
    render(<AdminProducto />);

    await userEvent.click(screen.getByRole('button', { name: /agregar nuevo producto/i }));

    await userEvent.type(screen.getByLabelText(/nombre del producto/i), 'Tomate Granel');
    await userEvent.type(screen.getByLabelText(/precio/i), '-500');
    await userEvent.type(screen.getByLabelText(/stock/i), '-10');

    await userEvent.click(screen.getByRole('button', { name: /crear producto/i }));

    expect(await screen.findByText(/el precio no puede ser negativo/i)).toBeInTheDocument();
    expect(screen.getByText(/el stock inicial debe ser mayor o igual a cero/i)).toBeInTheDocument();
  });

  test('debe completar el flujo completo de alta exitosa de un nuevo producto', async () => {
    render(<AdminProducto />);

    await userEvent.click(screen.getByRole('button', { name: /agregar nuevo producto/i }));

    await userEvent.type(screen.getByLabelText(/nombre del producto/i), 'Bebida Cola 3L');
    await userEvent.type(screen.getByLabelText(/precio/i), '2490');
    await userEvent.type(screen.getByLabelText(/stock/i), '40');
    
    const selectCategoria = screen.getByLabelText(/categoría/i);
    await userEvent.selectOptions(selectCategoria, 'Bebestibles');

    await userEvent.click(screen.getByRole('button', { name: /crear producto/i }));

    expect(await screen.findByText('Bebida Cola 3L')).toBeInTheDocument();
    expect(screen.getByText('$2.490')).toBeInTheDocument();
    expect(screen.getByText(/producto creado con éxito/i)).toBeInTheDocument();
  });
});