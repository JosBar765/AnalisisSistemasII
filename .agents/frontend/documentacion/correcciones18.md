# Corrección 18 — El logo de MediSistema en el menú lateral y en la pestaña del navegador (Frontend)

## Problema

- El recuadro junto a "MediSistema" en el menú lateral mostraba un ícono genérico de capas (un SVG de ejemplo).
- La pestaña del navegador mostraba el favicon por defecto de Angular.

## Solución

- **Menú lateral** (`main-layout.component.html`): el recuadro `.sidebar-brand-icon` ahora contiene `logo.png` (el caduceo,
  el mismo de la pantalla de login) en lugar del SVG. Como el logo es negro sobre fondo transparente, el recuadro pasó de
  verde a **blanco** con un padding de 4 px (`styles.css`), y la imagen usa `.sidebar-brand-logo` (`object-fit: contain`).
  Aplica a los tres roles (el layout es el mismo).
- **Pestaña del navegador:** se reemplazó `frontend/public/favicon.ico` por uno generado a partir del logo (16, 32, 48 y
  64 px) y se agregó `public/icon-256.png`. Son un cuadrado blanco de esquinas redondeadas con el caduceo centrado,
  para que se vea también en pestañas con tema oscuro. `index.html` enlaza ambos (`favicon.ico`, `icon-256.png` y
  `apple-touch-icon`) con `?v=2` para saltarse el ícono en caché.

## Archivos

```
frontend/public/favicon.ico (reemplazado), frontend/public/icon-256.png (nuevo)
frontend/src/index.html
frontend/src/app/layouts/main/main-layout.component.html
frontend/src/styles.css
```

## Consideraciones

- Los navegadores guardan los favicons agresivamente: si la pestaña sigue mostrando el de Angular, recarga con Ctrl+F5 o
  cierra y vuelve a abrir la pestaña. Para cambiar el ícono otra vez, subir el número de `?v=`.
- Los íconos se generaron una sola vez con Pillow desde `logo.png`; si cambia el logo hay que regenerarlos.
- Verificado en el navegador: el logo aparece en el menú. El favicon de la pestaña se verificó solo comprobando que el
  servidor entrega los archivos (`index.html` apunta a ellos y responden 200); no se pudo ver en la pestaña.
