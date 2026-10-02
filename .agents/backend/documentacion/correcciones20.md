# Corrección 20 — Reprogramar una cita ya atendida crea una cita nueva (Backend)

> Reemplaza lo dicho en `correcciones18.md` ("una cita Atendido sigue sin poder reprogramarse").

## Problema / decisión

El usuario quiere poder reprogramar también citas ya atendidas. Moverlas tal cual alteraría la historia clínica: la
consulta (diagnóstico, signos vitales) toma su fecha de la cita, y `Consulta.id_cita` es único, por lo que la cita
movida no podría volver a atenderse. **Decisión del usuario:** reprogramar una atendida crea una cita nueva.

## Solución

`CitaServiceImpl.reprogramar`, si la cita está "Atendido", llama a `programarSeguimiento`:

- La cita atendida **queda intacta** (estado, fecha, hora y consulta originales).
- Se crea una cita **"En espera"** con el mismo paciente y médico en la nueva fecha y hora, con las mismas validaciones
  que una cita nueva (horario disponible del médico y reglas del paciente por día, `correcciones16.md`).
  La atendida cuenta para esas reglas: no se puede crear el seguimiento el mismo día con el mismo médico.
- La respuesta es la **cita nueva** (otro `id`) y se publica `CITA_CREADA`.
- "En espera" y "Cancelado" siguen moviéndose en la misma cita (`correcciones18.md`).

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/services/impl/CitaServiceImpl.java`

## Resultado (verificado)

Reprogramar la atendida de un paciente al 12/10 11:30 → 200 con una cita nueva "En espera"; la original sigue
"Atendido" en su fecha. Reprogramarla al mismo día con el mismo médico → 400.

## Consideraciones

- Flujo de estados: `Programar → En espera ⇄ Cancelado`, `En espera → Atendido`, y `Atendido → (cita nueva)`.
- Pantalla: `frontend/documentacion/correcciones15.md`.
