# Corrección 11 — Buscadores sin distinguir mayúsculas ni tildes, y buscador en Documentos clínicos (Frontend)

## Problema

1. Los buscadores comparaban con `toLowerCase()`, que ignora mayúsculas pero **no tildes**: buscar "Josue" no
   encontraba a "Josué".
2. En la pantalla `Documentos clínicos` de la secretaria el paciente se elegía solo con un combo (todos los
   pacientes), sin forma de filtrarlos.

## Solución

- Nueva utilidad `shared/utils/busqueda.ts`: `normalizarBusqueda` (quita tildes con `normalize('NFD')`, pasa a
  minúsculas y recorta; la ñ pierde su tilde) y `coincideBusqueda(texto, busqueda)` (búsqueda vacía = coincide con
  todo). Se extrajo a `shared` porque la usan 5 pantallas (regla DRY de 4 o más).
- Se aplicó a **todos** los buscadores: usuarios (nombres, apellidos y correo; antes no incluía los segundos nombres
  ni apellidos), pacientes (DPI, nombre, teléfono), expediente (DPI, nombre, teléfono), el filtro de paciente del
  modal `Programar Cita` y el nuevo de Documentos.
- **Documentos clínicos:** campo "Filtrar por DPI o nombre..." sobre el combo de pacientes, con el mismo estilo que el
  del modal de citas. El paciente ya seleccionado se conserva siempre en la lista aunque no coincida con el filtro,
  para que el combo no parezca perderlo.

## Archivos modificados

```
shared/utils/busqueda.ts (nuevo)
features/admin/pages/usuarios/usuarios.component.ts
features/pacientes/pages/listar/listar.component.ts
features/expediente/pages/buscar/buscar.component.ts
features/citas/components/cita-form-modal/cita-form-modal.component.ts
features/documentos/pages/listar/listar.component.ts y .html
```

## Resultado (verificado en el navegador)

Documentos clínicos: `JoSue` lista a "Josué Abraham…" y "Josué Pérez…" (con tilde) y oculta al resto; `44444` deja solo
al paciente con ese DPI. Usuarios: `JULIAN` encuentra a "Julían Montenegro". `ng build` sin errores.

## Consideraciones

- Los buscadores nuevos deben usar `coincideBusqueda`, no `toLowerCase().includes(...)`.
- Solo se normalizan tildes y mayúsculas; no hay búsqueda aproximada (errores de tipeo).
