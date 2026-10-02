# Corrección 14 — Botón "Reprogramar" en las citas canceladas (Frontend)

> Acompaña a `backend/documentacion/correcciones18.md`.

## Problema

En la agenda de citas de la secretaria (`/citas`) las citas canceladas mostraban "Sin acciones disponibles".

## Solución

En `agenda.component.html` la columna de acciones queda así, según el estado de la cita:

| Estado | Acciones |
| --- | --- |
| En espera | Reprogramar y Cancelar |
| **Cancelado** | **Reprogramar** |
| Atendido | "Sin acciones disponibles" |

Reprogramar abre el mismo modal de siempre (médico y paciente no editables, fecha y horarios disponibles). Al guardar,
la cita vuelve a "En espera" y la tabla se actualiza (también en vivo por WebSocket). Se agregó el ayudante
`estaCancelada` en `cita.model.ts`, junto a `estaEnEspera`.

## Archivos modificados

```
features/citas/pages/agenda/agenda.component.html y .ts
features/citas/models/cita.model.ts
```

## Consideraciones

- Verificado con `ng build` y el flujo de la API; **no se probó el botón en el navegador**.
- El rechazo por horario ocupado o por las reglas del paciente aparece en el aviso emergente del formulario.
