import { cleanup, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'

import Productos from './Productos.jsx'

// ========================================
// 1. CONFIGURACIÓN Y HELPER
// ========================================

function mostrar() {
    render(<Productos />)
    return {
        user: userEvent.setup(),
    }
}

beforeEach(() => {
    localStorage.clear()
})

afterEach(() => {
    cleanup()
    vi.restoreAllMocks()
})

// ========================================
// 2. PRUEBAS DE RENDERIZADO E INICIALIZACIÓN
// ========================================

describe('Pruebas unitarias para Catálogo de Productos', () => {
    test('renderiza la lista completa de productos con sus precios e imágenes', () => {
        mostrar()

        expect(
            screen.getByRole('heading', { name: /Todos Nuestros Productos/i })
        ).toBeInTheDocument()

        // Comprobar la presencia de los 4 productos
        expect(screen.getByText('Pan amasado casero')).toBeInTheDocument()
        expect(screen.getByText('Queso gauda laminado')).toBeInTheDocument()
        expect(screen.getByText('Palta hass madura')).toBeInTheDocument()
        expect(screen.getByText('Leche entera Colun')).toBeInTheDocument()

        // Comprobar imágenes
        expect(screen.getByAltText('Pan amasado casero')).toBeInTheDocument()
        expect(screen.getByAltText('Queso gauda laminado')).toBeInTheDocument()

        // Comprobar formato de precios
        expect(screen.getByText('$1.500')).toBeInTheDocument()
        expect(screen.getByText('$3.200')).toBeInTheDocument()
        expect(screen.getByText('$990')).toBeInTheDocument()
        expect(screen.getByText('$1.190')).toBeInTheDocument()

        // Inicialmente no debe haber botones de disminuir ni contadores
        expect(
            screen.queryByRole('button', { name: /Disminuir cantidad/i })
        ).not.toBeInTheDocument()
    })

    test('carga el estado inicial del carrito desde localStorage si ya existen productos', () => {
        const carritoPrevio = [
            {
                id: 1,
                nombre: 'Pan amasado casero',
                precio: 1500,
                cantidad: 2,
            },
        ]
        localStorage.setItem('carritoMarket', JSON.stringify(carritoPrevio))

        mostrar()

        // Debe mostrar la cantidad '2' para el primer producto
        expect(screen.getByText('2')).toBeInTheDocument()

        // Debe aparecer exactamente un botón para disminuir
        const botonesDisminuir = screen.getAllByRole('button', {
            name: /Disminuir cantidad/i,
        })
        expect(botonesDisminuir).toHaveLength(1)
    })

    // ========================================
    // 3. INTERACCIÓN Y LÓGICA DEL CARRITO
    // ========================================

    test('agrega un producto nuevo al carrito e incrementa su cantidad al presionar +', async () => {
        const eventoSpy = vi.fn()
        window.addEventListener('carritoActualizado', eventoSpy)

        const { user } = mostrar()

        const botonesAumentar = screen.getAllByRole('button', {
            name: /Aumentar cantidad/i,
        })

        // Primer clic en 'Pan amasado casero'
        await user.click(botonesAumentar[0])
        expect(screen.getByText('1')).toBeInTheDocument()

        // Segundo clic en 'Pan amasado casero'
        await user.click(botonesAumentar[0])
        expect(screen.getByText('2')).toBeInTheDocument()

        // Comprobar actualización en localStorage
        const carritoGuardado = JSON.parse(
            localStorage.getItem('carritoMarket') || '[]'
        )
        expect(carritoGuardado).toHaveLength(1)
        expect(carritoGuardado[0].id).toBe(1)
        expect(carritoGuardado[0].cantidad).toBe(2)

        // Evento global para el Navbar
        expect(eventoSpy).toHaveBeenCalled()

        window.removeEventListener('carritoActualizado', eventoSpy)
    })

    test('disminuye la cantidad al presionar - y elimina el producto cuando llega a 0', async () => {
        const { user } = mostrar()

        const botonesAumentar = screen.getAllByRole('button', {
            name: /Aumentar cantidad/i,
        })

        // Agregamos 'Pan amasado casero' (index 0) y 'Queso gauda laminado' (index 1)
        await user.click(botonesAumentar[0])
        await user.click(botonesAumentar[1])

        expect(screen.getAllByText('1')).toHaveLength(2)

        // Aumentamos Pan a 2
        await user.click(botonesAumentar[0])
        expect(screen.getByText('2')).toBeInTheDocument()

        let botonesDisminuir = screen.getAllByRole('button', {
            name: /Disminuir cantidad/i,
        })
        expect(botonesDisminuir).toHaveLength(2)

        // Restamos 1 al Pan (pasa de 2 a 1)
        await user.click(botonesDisminuir[0])
        expect(screen.queryByText('2')).not.toBeInTheDocument()

        // Restamos 1 al Pan nuevamente (pasa de 1 a 0 y debe filtrarse/eliminarse del carrito)
        botonesDisminuir = screen.getAllByRole('button', {
            name: /Disminuir cantidad/i,
        })
        await user.click(botonesDisminuir[0])

        // Ahora solo debe quedar un botón de disminuir (el del Queso)
        expect(
            screen.getAllByRole('button', { name: /Disminuir cantidad/i })
        ).toHaveLength(1)

        const carritoGuardado = JSON.parse(
            localStorage.getItem('carritoMarket') || '[]'
        )
        expect(carritoGuardado).toHaveLength(1)
        expect(carritoGuardado[0].id).toBe(2) // Queda únicamente el id 2 (Queso)
    })
})