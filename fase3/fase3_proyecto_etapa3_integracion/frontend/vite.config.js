import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true, // Esto le enseña a Vitest qué es "expect"
    setupFiles: './src/setupTests.js', // Aquí le decimos dónde están tus mocks
  }
});