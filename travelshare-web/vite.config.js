import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/strategy': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/travel-notice': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/route': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/partner': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/comment': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/city': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/attraction': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/home': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/upload': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/bill': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
