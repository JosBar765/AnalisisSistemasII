# Corrección 16 — Un paciente no puede tener dos citas con el mismo médico ni a la misma hora el mismo día (Backend)

> Versión final de esta corrección. La primera versión prohibía **cualquier** segunda cita del paciente en un día;
> por decisión del usuario se relajó (ver "Solución").

## Problema

`CitaServiceImpl` solo validaba que el horario estuviera libre **para el médico**. Un mismo paciente podía tener
varias citas el mismo día con el mismo médico, o dos citas a la misma hora con médicos distintos (estando físicamente
en dos consultorios a la vez).

## Regla

Un paciente **sí** puede tener varias citas el mismo día, pero solo si son con **médicos distintos Y a horas
distintas**. Ejemplo: cardiólogo a las 11:00 y gastroenterólogo a las 12:30 el mismo día es válido.

## Solución

`validarCitasDelPacienteElMismoDia` en `CitaServiceImpl`, invocada al **programar** y al **reprogramar**, antes de
validar el horario del médico. Revisa las demás citas del paciente ese día cuyo estado **no sea "Cancelado"**
(`CitaRepository.findByPacienteEntityIdAndFechaAndEstadoCitaEntityEstadoCitaNot`; cuentan "En espera" y "Atendido") y
rechaza (400) si alguna:

- es con **el mismo médico**: "El paciente ya tiene una cita con este médico el 2026-10-05 (a las 11:00). Un paciente
  solo puede tener varias citas el mismo día con médicos distintos."
- es a **la misma hora** (con otro médico): "El paciente ya tiene otra cita el 2026-10-05 a las 11:00 con otro médico.
  Un paciente no puede tener dos citas a la misma hora."

Al reprogramar se excluye la propia cita, y se valida con el médico de la cita y la nueva fecha y hora.

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/repositories/CitaRepository.java`
- `backend/src/main/java/com/josbar/medisistemas/services/impl/CitaServiceImpl.java`

## Resultado (verificado con el Backend en ejecución, con dos médicos con jornada el mismo lunes)

Paciente con cita con el médico A a las 11:00: otra con el médico A a las 12:30 → 400 (mismo médico); con el médico B
a las 11:00 → 400 (misma hora); con el médico B a las 12:30 → **201**. Reprogramar la cita del médico B a las 11:00 →
400; a las 12:00 → 200. Cancelar la cita del médico A y pedir otra con él a las 11:00 → 201 (las canceladas no cuentan);
con B ya en las 12:00, intentar A a las 12:00 → 400 y B a las 12:30 → 400.

## Consideraciones

- Solo compara la **hora de inicio** exacta, no el solapamiento por duración: dos citas de 30 minutos a las 11:00 y a
  las 11:15 de médicos con jornadas de 15 minutos no se detectarían como cruzadas (hoy las citas de un médico son
  múltiplos de su duración, pero médicos con duraciones distintas podrían cruzarse).
- Es una regla del servicio; la base de datos no la impide (un `INSERT` directo puede duplicar). Hacerlo a nivel de
  base requeriría restricciones únicas sobre `(id_paciente, fecha, id_medico)` y `(id_paciente, fecha, hora)` que
  excluyan las canceladas, y hoy el estado es una clave foránea a `EstadoCita`, no un valor que un índice pueda evaluar.
- Las citas que ya estuvieran duplicadas antes de este cambio no se corrigen.
- El frontend no cambió: el rechazo aparece en el aviso emergente del formulario de la cita.
