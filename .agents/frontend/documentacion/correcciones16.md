# Corrección 16 — Solo un llamado a sala a la vez en la agenda del médico (Frontend)

> Acompaña a `backend/documentacion/correcciones21.md`.

## Problema

En "Mi Agenda y Cola" el médico podía solicitar el llamado de varios pacientes presentes seguidos.

## Solución

En `agenda-medica/pages/agenda/agenda.component.html`, mientras haya un llamado pendiente (`llamados().length > 0`):

- El botón principal "Solicitar Llamar Siguiente Paciente" queda deshabilitado.
- El botón "Solicitar Llamado" de cada paciente en la cola queda deshabilitado, con una ayuda al pasar el mouse:
  "Atienda al paciente ya llamado antes de llamar al siguiente".
- El paciente ya llamado conserva su botón "Atender Consulta" y el banner "Solicitud enviada a Recepción".

Al atender la consulta, o cuando la secretaria cancele o reprograme esa cita, el llamado desaparece (la agenda se
actualiza en vivo) y los botones se habilitan de nuevo.

## Archivos modificados

- `frontend/src/app/features/agenda-medica/pages/agenda/agenda.component.html`

## Consideraciones

- La validación real está en el Backend: aunque se evada el botón, el servidor responde 400 y el aviso emergente muestra
  el motivo.
- Verificado con `ng build` y por API; los botones deshabilitados no se probaron en el navegador.
