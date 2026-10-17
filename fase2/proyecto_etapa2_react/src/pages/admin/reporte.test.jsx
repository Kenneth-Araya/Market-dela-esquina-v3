import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AdminReporte from '../pages/admin/AdminReporte';
import '@testing-library/jest-dom';

describe('AdminReporte - Suite Completa', () => {
  test('debe renderizar los filtros de fecha y el selector de tipo de reporte por defecto', async () => {
    render(<AdminReporte />);

    expect(await screen.findByLabelText(/fecha inicio/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/fecha fin/i)).toBeInTheDocument();
    expect(screen.getByRole('combobox', { name: /tipo de reporte/i })).toHaveValue('ventas_generales');
  });

  test('debe mostrar un mensaje de error si el rango de fechas es inválido (fecha fin menor a fecha inicio)', async () => {
    render(<AdminReporte />);

    const inputInicio = screen.getByLabelText(/fecha inicio/i);
    const inputFin = screen.getByLabelText(/fecha fin/i);

    await userEvent.type(inputInicio, '2026-10-10');
    await userEvent.type(inputFin, '2026-10-01');

    await userEvent.click(screen.getByRole('button', { name: /generar reporte/i }));

    expect(await screen.findByText(/la fecha de fin no puede ser anterior a la de inicio/i)).toBeInTheDocument();
  });

  test('debe generar y renderizar correctamente la tabla de datos y las sumatorias totales', async () => {
    render(<AdminReporte />);

    const selectReporte = screen.getByRole('combobox', { name: /tipo de reporte/i });
    await userEvent.selectOptions(selectReporte, 'ventas_mensuales');

    await userEvent.click(screen.getByRole('button', { name: /generar reporte/i }));

    expect(await screen.findByText(/resumen de ventas mensuales/i)).toBeInTheDocument();
    expect(screen.getByTestId('tabla-reporte-datos')).toBeInTheDocument();
    expect(screen.getByTestId('total-recaudado-periodo')).toHaveTextContent('$12.500.000');
    expect(screen.getByRole('button', { name: /exportar a csv/i })).toBeInTheDocument();
  });
});