# Corrección 15 — Reprogramar disponible en toda cita, incluidas las atendidas (Frontend)

> Acompaña a `backend/documentacion/correcciones20.md`. Reemplaza la tabla de acciones de `correcciones14.md`.

## Solución

En la agenda de citas de la secretaria (`/citas`):

| Estado | Acciones |
| --- | --- |
| En espera | Reprogramar y Cancelar |
| Cancelado | Reprogramar |
| Atendido | Reprogramar (crea una cita nueva) |

Se eliminó el texto "Sin acciones disponibles" y el ayudante `estaCancelada` de `cita.model.ts`, que ya no se usa.
Cuando la cita reprogramada está atendida, el modal muestra un aviso: "Esta cita ya fue atendida y se conservará con su
consulta. Al guardar se creará una cita nueva con el mismo paciente y médico en la fecha y hora que elija."
(`esSeguimiento` en `cita-form-modal.component.ts`).

## Archivos modificados

```
features/citas/pages/agenda/agenda.component.html y .ts
features/citas/models/cita.model.ts
features/citas/components/cita-form-modal/cita-form-modal.component.html y .ts
```

## Consideraciones

- Verificado con `ng build` y el flujo de la API; los botones y el aviso no se probaron en el navegador.
