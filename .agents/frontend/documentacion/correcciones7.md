# Corrección 7 — Errores de acciones en un aviso emergente (SweetAlert2) (Frontend)

> Continúa `correcciones6.md` (que hizo llegar el mensaje real del Backend a la pantalla).

## Problema

El mensaje de un error al guardar o ejecutar una acción se mostraba como una franja roja **en la página**, detrás
del fondo oscuro del modal abierto, o dentro del propio modal cuando la pantalla lo había previsto. Había que
cerrar el formulario para verlo.

## Solución

- Dependencia nueva **`sweetalert2`** (`package.json`). Para evitar la advertencia de módulo no-ESM se agregó
  `allowedCommonJsDependencies: ["sweetalert2"]` en `angular.json`.
- `core/services/notification.service.ts` (`NotificationService`, el servicio global previsto en
  `reglas_estructura_y_features.md`): `error(mensaje, titulo?)` abre un aviso emergente con ícono de error, el
  mensaje y el botón "Entendido" (con las clases `btn btn-primary` de la aplicación). Aparece **encima** del
  modal y, al cerrarlo, el formulario sigue abierto con todo lo escrito.
- Las acciones que fallan llaman a `notificacion.error(mensajeDeError(err, 'texto por defecto'))`: usuarios,
  médicos, jornadas, especialidades (guardar, eliminar, cambiar estado), pacientes (guardar, estado), citas
  (guardar, cancelar, horarios), llegada del paciente, solicitud de llamado, documentos (subir, reemplazar, ver),
  consulta (registrar, modificar) y expediente (ver documento).
- Se **eliminó** lo que quedó sin uso: la señal/entrada `error` y su franja de alerta en `paciente-form`,
  `modificar-consulta-modal`, `cita-form-modal`, `subir-documento-modal` y `reemplazar-documento-modal`, y las
  señales `errorFormulario` de `pacientes/listar` y `consultas/detalle`.

## Qué sigue siendo en línea

Los errores de **carga** de datos (listados, "no se encontró la cita/consulta") conservan su franja en la página:
no ocurren dentro de un formulario. El login conserva su mensaje bajo el formulario.

## Archivos modificados

```
core/services/notification.service.ts (nuevo)
package.json, package-lock.json, angular.json
features/admin/pages/{usuarios,medicos,jornadas,especialidades}/*.component.ts
features/{pacientes,citas,documentos,consultas,expediente,agenda-medica}/... (componentes y modales indicados arriba)
```

## Consideraciones

- Para un error nuevo basta inyectar `NotificationService` y llamar a `error(...)`; no crear señales de error por
  formulario.
- SweetAlert2 inyecta sus propios estilos y usa `z-index: 1060`, por encima de los modales (`z-index: 100`).
- Verificado en el navegador: al guardar un usuario con un teléfono ya usado aparece el aviso "Ya existe un usuario
  registrado con el número de teléfono «…»" sobre el modal y el formulario conserva sus datos. `ng build` sin errores.
