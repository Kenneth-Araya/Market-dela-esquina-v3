import { createContext, useContext, useEffect, useState } from 'react'
import { leer, guardar } from '../utils/storage'

const CartContext = createContext(null)

// Carrito global: [{ id, cantidad }] guardado en localStorage ("carrito")
export function CartProvider({ children }) {
  const [carrito, setCarrito] = useState(() => leer('carrito', []))

  useEffect(() => {
    guardar('carrito', carrito)
  }, [carrito])

  const cambiarCantidad = (id, delta) => {
    setCarrito((actual) => {
      const item = actual.find((p) => p.id === id)
      if (!item) return delta > 0 ? [...actual, { id, cantidad: 1 }] : actual
      return actual
        .map((p) => (p.id === id ? { ...p, cantidad: p.cantidad + delta } : p))
        .filter((p) => p.cantidad > 0)
    })
  }

  const eliminar = (id) => setCarrito((actual) => actual.filter((p) => p.id !== id))
  const vaciar = () => setCarrito([])
  const cantidadDe = (id) => carrito.find((p) => p.id === id)?.cantidad ?? 0
  const totalUnidades = carrito.reduce((acc, p) => acc + p.cantidad, 0)

  return (
    <CartContext.Provider value={{ carrito, cambiarCantidad, eliminar, vaciar, cantidadDe, totalUnidades }}>
      {children}
    </CartContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export const useCart = () => useContext(CartContext)
