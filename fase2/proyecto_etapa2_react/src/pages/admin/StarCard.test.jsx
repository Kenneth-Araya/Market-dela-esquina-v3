import { render, screen, waitFor } from '@testing-library/react';
import AdminStarCards from '../pages/admin/AdminStarCards';
import '@testing-library/jest-dom';

describe('AdminStarCards - Suite Completa', () => {
  test('debe mostrar el estado de carga (skeleton/spinner) antes de recibir la información de los KPIs', () => {
    render(<AdminStarCards />);
    expect(screen.getByTestId('loading-spinner-cards')).toBeInTheDocument();
  });

  test('debe renderizar de forma correcta todas las Star Cards con sus valores e indicadores de tendencia', async () => {
    render(<AdminStarCards />);

    // Total de Ventas
    const cardVentas = await screen.findByTestId('card-total-sales');
    expect(cardVentas).toHaveTextContent('$1.500.000');
    expect(screen.getByText(/\+12% vs mes anterior/i)).toBeInTheDocument();

    // Pedidos Pendientes
    const cardPedidos = await screen.findByTestId('card-pending-orders');
    expect(cardPedidos).toHaveTextContent('12');

    // Clientes Activos
    const cardClientes = await screen.findByTestId('card-active-clients');
    expect(cardClientes).toHaveTextContent('340');

    // Tasa de Conversión
    const cardConversacion = await screen.findByTestId('card-conversion-rate');
    expect(cardConversacion).toHaveTextContent('4.8%');
  });

  test('debe aplicar clases visuales de alerta si los pedidos pendientes superan el límite crítico', async () => {
    render(<AdminStarCards />);
    const cardPedidos = await screen.findByTestId('card-pending-orders');
    // Si la data mock de prueba elevara los pendientes a un número alto
    expect(cardPedidos.closest('.card')).toHaveClass('border-warning');
  });
});