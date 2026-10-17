import { cleanup, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'

import Carrito from './Carrito.jsx'

// ========================================
// 1. CONFIGURACIÓN Y MOCKS
// ========================================

const mockProductos = [
  {
    id: 1,
    nombre: 'Pan amasado',
    precio: '$1.500', // String con formato
    cantidad: 2,
    img: 'pan.jpg',
  },
  {
    id: 2,
    nombre: 'Leche entera',
    precio: 1000, // Número directo
    cantidad: 1,
    img: 'leche.jpg',
  },
  {
    id: 3,
    nombre: 'Regalo promocional',
    precio: 'Gratis', // String inválido matemáticamente (forzará el fallback a || 0)
    cantidad: 1,
    img: 'regalo.jpg',
  },
]

function mostrar() {
  render(
    <MemoryRouter>
      <Carrito />
    </MemoryRouter>
  )
  return {
    user: userEvent.setup(),
  }
}

beforeEach(() => {
  localStorage.clear()
  vi.spyOn(window, 'alert').mockImplementation(() => {})
})

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
})

// ========================================
// 2. PRUEBAS DE CARRITO VACÍO
// ========================================

describe('Pruebas unitarias para Carrito de Compras', () => {
  test('renderiza correctamente el carrito vacío', () => {
    mostrar()

    expect(screen.getByText('Tu Carrito de Compras')).toBeInTheDocument()
    expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument()
    
    // CORRECCIÓN: Hay dos '$0' en pantalla (Subtotal y Total)
    const montosCero = screen.getAllByText('$0')
    expect(montosCero).toHaveLength(2)
  })

  test('finalizar compra con carrito vacío muestra alerta de error y vaciar carrito no hace nada', async () => {
    const { user } = mostrar()

    // Intentar finalizar compra
    await user.click(screen.getByRole('button', { name: /Finalizar Venta/i }))
    expect(window.alert).toHaveBeenCalledWith(
      'Tu carrito está vacío. Agrega productos antes de finalizar la venta.'
    )

    // Intentar vaciar carrito (dispara la condición if (carrito.length === 0) return)
    const btnVaciar = screen.getByRole('button', { name: /Vaciar Carrito/i })
    await user.click(btnVaciar)
    
    expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument()
  })

  // ========================================
  // 3. PRUEBAS CON PRODUCTOS Y CÁLCULOS
  // ========================================

  test('renderiza productos desde localStorage y calcula el total correctamente', () => {
    localStorage.setItem('carritoMarket', JSON.stringify(mockProductos))
    mostrar()

    expect(screen.getByText('Pan amasado')).toBeInTheDocument()
    expect(screen.getByText('Leche entera')).toBeInTheDocument()
    expect(screen.getByText('Regalo promocional')).toBeInTheDocument()

    // Cálculos: (1500 * 2) + (1000 * 1) + (0 * 1) = 4000
    // Hay 1 subtotal y 1 total con el valor 4000, además de los subtotales individuales
    const montosTotales = screen.getAllByText('$4.000')
    expect(montosTotales.length).toBeGreaterThan(0)
  })

  // ========================================
  // 4. CONTROLES DE UNIDADES (+ Y -)
  // ========================================

  test('aumenta la cantidad de un producto y actualiza localStorage y evento global', async () => {
    localStorage.setItem('carritoMarket', JSON.stringify([mockProductos[1]])) // Leche (cantidad 1)
    
    const eventoActualizadoSpy = vi.fn()
    window.addEventListener('carritoActualizado', eventoActualizadoSpy)

    const { user } = mostrar()

    const btnAumentar = screen.getByLabelText('Sumar una unidad')
    await user.click(btnAumentar)

    // La cantidad en pantalla debería subir a 2
    expect(screen.getByText('2')).toBeInTheDocument()

    // El precio de 1000 sube a 2000
    expect(screen.getAllByText('$2.000').length).toBeGreaterThan(0)

    // Verifica que el localStorage se guardó
    const carritoGuardado = JSON.parse(localStorage.getItem('carritoMarket'))
    expect(carritoGuardado[0].cantidad).toBe(2)

    // El useEffect dispara el evento
    expect(eventoActualizadoSpy).toHaveBeenCalled()
    window.removeEventListener('carritoActualizado', eventoActualizadoSpy)
  })

  test('disminuye la cantidad de un producto, y lo elimina si llega a 0', async () => {
    localStorage.setItem('carritoMarket', JSON.stringify([mockProductos[0], mockProductos[1]])) 
    // Pan (cant: 2), Leche (cant: 1)

    const { user } = mostrar()

    const botonesRestar = screen.getAllByLabelText('Restar una unidad')
    
    // Restar Pan (botón índice 0) -> Baja de 2 a 1
    await user.click(botonesRestar[0])
    
    let carritoGuardado = JSON.parse(localStorage.getItem('carritoMarket'))
    expect(carritoGuardado[0].cantidad).toBe(1)
    expect(screen.getByText('Pan amasado')).toBeInTheDocument()

    // Restar Leche (botón índice 1) -> Baja de 1 a 0 (debe desaparecer)
    await user.click(botonesRestar[1])

    carritoGuardado = JSON.parse(localStorage.getItem('carritoMarket'))
    expect(carritoGuardado.length).toBe(1) // Solo queda el Pan
    expect(screen.queryByText('Leche entera')).not.toBeInTheDocument()
  })

  // ========================================
  // 5. ACCIONES GLOBALES: QUITAR, VACIAR Y FINALIZAR
  // ========================================

  test('elimina un producto por completo al presionar "Quitar todo"', async () => {
    localStorage.setItem('carritoMarket', JSON.stringify(mockProductos))
    const { user } = mostrar()

    const botonesQuitarTodo = screen.getAllByRole('button', { name: /Quitar todo/i })
    
    // Eliminamos el Pan (índice 0)
    await user.click(botonesQuitarTodo[0])

    expect(screen.queryByText('Pan amasado')).not.toBeInTheDocument()
    const carritoGuardado = JSON.parse(localStorage.getItem('carritoMarket'))
    expect(carritoGuardado.length).toBe(2) // Quedan 2 productos
  })

  test('vacía todo el carrito usando el botón "Vaciar Carrito"', async () => {
    localStorage.setItem('carritoMarket', JSON.stringify(mockProductos))
    const { user } = mostrar()

    const btnVaciar = screen.getByRole('button', { name: /Vaciar Carrito/i })
    await user.click(btnVaciar)

    expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument()
    const carritoGuardado = JSON.parse(localStorage.getItem('carritoMarket'))
    expect(carritoGuardado).toEqual([])
  })

  test('finaliza la compra exitosamente y vacía el carrito', async () => {
    localStorage.setItem('carritoMarket', JSON.stringify(mockProductos))
    const { user } = mostrar()

    const btnFinalizar = screen.getByRole('button', { name: /Finalizar Venta/i })
    await user.click(btnFinalizar)

    expect(window.alert).toHaveBeenCalledWith(
      '¡Compra realizada con éxito! Gracias por preferir Market De La Esquina.'
    )

    // El carrito debe quedar limpio en pantalla y en localStorage
    expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument()
    const carritoGuardado = JSON.parse(localStorage.getItem('carritoMarket'))
    expect(carritoGuardado).toEqual([])
  })
})