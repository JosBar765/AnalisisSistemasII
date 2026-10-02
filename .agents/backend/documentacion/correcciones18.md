# Corrección 18 — Las citas canceladas se pueden reprogramar (Backend)

## Problema

`CitaServiceImpl.reprogramar` solo aceptaba citas en estado "En espera". Una cita cancelada quedaba sin salida: no
se podía volver a usar y la secretaria tenía que programar una nueva.

## Regla nueva (decisión del usuario)

Una cita **cancelada** se puede reprogramar. Al hacerlo vuelve a estado **"En espera"**. Una cita **"Atendido"** no se cancela;
reprogramarla crea una cita nueva (`correcciones20.md`).

Flujo de estados resultante:

```
Programar → En espera ⇄ Cancelado          (cancelar / reprogramar)
            En espera → Atendido            (registrar la consulta)
```

## Solución

`CitaServiceImpl.reprogramar`:

- Acepta estado "En espera" o "Cancelado"; con cualquier otro responde 400: "Solo se puede reprogramar una cita en
  estado 'En espera' o 'Cancelado'."
- Aplica las mismas validaciones de siempre a la nueva fecha y hora: horario disponible del médico y reglas del
  paciente por día (`correcciones16.md`), excluyendo la propia cita.
- Limpia la llegada y el llamado, como antes, y si venía cancelada la pasa a "En espera".
- Publica `CITA_ACTUALIZADA` por WebSocket (Inicio, Agenda y Llamador se actualizan; el médico la vuelve a ver).
- Se agregó `findPorId`; `cancelar`, `registrarLlegada` y `solicitarLlamado` siguen exigiendo "En espera".

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/services/impl/CitaServiceImpl.java`

## Resultado (verificado con el Backend en ejecución)

Cancelar → reprogramar la cancelada a otra hora (200, estado "En espera") → cancelar de nuevo → reprogramarla a otro
día (200, "En espera"). Reprogramar una cita "Atendido" → 400 (**cambió después**: `correcciones20.md`).

## Consideraciones

- Las citas canceladas no ocupaban horario ni contaban para las reglas por paciente, así que reprogramar compite por un
  horario como cualquier cita nueva y puede ser rechazada si ya está ocupado.
- El conteo del dashboard no baja al reprogramar: usa el historial `veces_cancelada` (`correcciones19.md`).
- Parte visual: `frontend/documentacion/correcciones14.md`.
