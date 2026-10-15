# Market De La Esquina

Proyecto en equipo: React + Vite + React Bootstrap + React Router.

## Puesta en marcha

```bash
git clone https://github.com/USUARIO/market-dela-esquina.git
cd market-dela-esquina
npm install
npm run dev        # http://localhost:5173
```

## Estructura

```
src/
  components/   Navigation, layouts y componentes reutilizables
  context/      estado global (ej. carrito)
  data/         datos estáticos (catálogo de productos)
  pages/        una página por ruta (pages/admin/ para el panel)
  styles/       archivos CSS
  utils/        funciones auxiliares
public/img/     imágenes
```

## Rutas

`/home` · `/productos` · `/nosotros` · `/blogs` · `/contacto` · `/carrito` · `/login` · `/registro`
`/admin` · `/admin/productos` · `/admin/usuarios` · `/admin/ventas` · `/admin/reportes` · `/admin/configuracion`

## Reparto de trabajo (editar)

| Integrante | Páginas |
|---|---|
| Kenneth Araya | _por definir_ |
| Bairon Valencia | Encargado de la vista del usuario |
| Jonathan Cornejo | _por definir_ |

## Flujo de trabajo con Git

1. Antes de empezar, actualiza `main`:
   ```bash
   git checkout main
   git pull
   ```
2. Crea una rama por tarea:
   ```bash
   git checkout -b feature/nombre-de-la-tarea
   ```
3. Haz commits pequeños y con mensaje claro:
   ```bash
   git add .
   git commit -m "Agrega página de contacto"
   ```
4. Sube tu rama y abre un **Pull Request** en GitHub:
   ```bash
   git push -u origin feature/nombre-de-la-tarea
   ```
5. Un compañero revisa y aprueba; luego se hace **Merge** a `main`.

Reglas: no hacer push directo a `main`, y ejecutar `npm run build` antes de abrir un Pull Request.

## Convenciones

- Componentes en `PascalCase` y un componente por archivo (`.jsx`).
- Usar clases de Bootstrap antes de escribir CSS propio.
- Imágenes en `public/img/` y se referencian como `/img/archivo.jpg`.
