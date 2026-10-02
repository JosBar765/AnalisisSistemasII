# Corrección 21 — El médico solo puede tener un llamado a sala a la vez (Backend)

## Problema

Con dos pacientes presentes (11:00 y 11:30) el médico podía pulsar "Solicitar llamado" para ambos: la secretaria
recibía dos llamados simultáneos.

## Regla

Un médico solo puede tener **un llamado pendiente** por día. Hasta que el paciente llamado sea atendido (la cita pasa a
"Atendido") o su cita se cancele o reprograme, no puede solicitar otro.

## Solución

`CitaServiceImpl.solicitarLlamado`, después de validar que la cita es del médico y que el paciente llegó, busca citas
del médico en esa fecha, "En espera" y con `hora_solicitud_llamado` ya registrada
(`CitaRepository.findByMedicoEntityIdAndFechaAndHoraSolicitudLlamadoIsNotNullAndEstadoCitaEntityEstadoCita`). Si existe
una (incluso la misma cita) responde 400: "Ya solicitó el llamado de <nombre>. Atienda a ese paciente antes de llamar al
siguiente."

Un llamado deja de estar pendiente solo, sin estado nuevo: al registrar la consulta la cita pasa a "Atendido"; cancelar
la cita cambia su estado; y reprogramar limpia `hora_solicitud_llamado`.

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/repositories/CitaRepository.java`
- `backend/src/main/java/com/josbar/medisistemas/services/impl/CitaServiceImpl.java`

## Resultado (verificado con dos pacientes presentes del mismo médico)

Primer llamado → 200; llamar al segundo → 400 con el mensaje; volver a llamar al primero → 400; cancelar al primero y
llamar al segundo → 200. También bloqueó correctamente contra el llamado pendiente que ya había en la base.

## Consideraciones

- Si un paciente llamado no se presenta, el médico lo descarta con "Cancelar llamado" (`correcciones22.md`).
- La regla es **por médico**; dos médicos distintos pueden tener un llamado cada uno.
- Los llamados duplicados que ya existieran antes de este cambio no se corrigen.
- Pantalla: `frontend/documentacion/correcciones16.md`.
