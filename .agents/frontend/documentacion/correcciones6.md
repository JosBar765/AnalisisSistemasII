# Corrección 6 — Mostrar la causa real de los errores de la API (Frontend)

> Acompaña a `backend/documentacion/correcciones12.md`.

## Problema

Las pantallas de administración descartaban la respuesta del Backend y mostraban textos fijos
("No se pudo guardar el usuario. Verifique los datos ingresados."), por lo que un teléfono duplicado era
indistinguible de cualquier otro error. El único caso especial (`especialidades`) comparaba el estado 400.

## Solución

- Nueva función `mensajeDeError(error, porDefecto)` en `shared/utils/mensaje-error.ts`: devuelve `error.error.message`
  de la respuesta de la API y, si no hay (sin conexión), el texto por defecto. Se extrajo porque el patrón se
  repetía en 10 lugares (regla DRY de 4 o más).
- Se usa en las operaciones de escritura que ocultaban el mensaje: usuarios (guardar, cambiar estado), médicos
  (guardar, cambiar estado), jornadas (guardar, eliminar), especialidades (guardar, eliminar), pacientes (cambiar
  estado) y la apertura de documentos (en `documentos` y `expediente`, p. ej. el 503 del almacenamiento).
- `especialidades` ya no compara `status === 400`: el mensaje de la regla de negocio llega del Backend.

> Actualización: estos mensajes ahora se muestran en un aviso emergente, no en una franja de la página
> (`correcciones7.md`).

## Archivos modificados

```
frontend/src/app/shared/utils/mensaje-error.ts (nuevo)
features/admin/pages/{usuarios,medicos,jornadas,especialidades}/*.component.ts
features/pacientes/pages/listar/listar.component.ts
features/documentos/pages/listar/listar.component.ts, features/expediente/pages/detalle/detalle.component.ts
```

## Consideraciones

- Las pantallas de secretaria y médico ya mostraban `err.error?.message`; no se tocaron. Los errores de **carga**
  (listados) conservan su texto fijo.
- Verificado con `ng build`. La comprobación en el navegador no se pudo completar (la pestaña dejó de
  responder); el contenido de los mensajes se verificó contra el Backend.
