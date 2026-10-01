# Corrección 5 — Especialidad del médico en el menú (Frontend)

> Acompaña a `backend/documentacion/correcciones11.md`.

## Problema

El menú lateral mostraba el rol (`MEDICO`) bajo el nombre; el mockup mostraba la especialidad.

## Solución

- Nuevo `core/services/perfil-medico.service.ts`: cuando la sesión es de un médico consulta `GET /medicos/me` y
  expone la señal `especialidad`; para otros roles queda en `null`. Es transversal (depende de la sesión), por eso
  vive en `core`.
- `MainLayoutComponent` la inyecta y el pie del menú muestra `especialidad() ?? rol`.

## Archivos modificados

```
frontend/src/app/core/services/perfil-medico.service.ts   (nuevo)
frontend/src/app/layouts/main/main-layout.component.ts, .html
```

## Consideraciones

- Si la consulta falla, se muestra el rol como antes.
- Se carga una vez por sesión; si el administrador cambia la especialidad se verá en el siguiente inicio de sesión.
- Verificado con `ng build`.
