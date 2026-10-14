import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // escucha en 0.0.0.0 (necesario para abrirlo desde la IP publica de EC2)
    port: 5173,
  },
  preview: {
    host: true,
    port: 5173,
  },
})
