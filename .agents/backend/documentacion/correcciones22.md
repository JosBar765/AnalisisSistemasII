# Corrección 22 — El médico puede cancelar su llamado a sala (Backend)

> Resuelve la limitación de `correcciones21.md` ("no existe un descartar llamado").

## Problema

Con la regla de un solo llamado a la vez, si el paciente llamado no se presentaba (o el médico se equivocaba de paciente)
el médico quedaba bloqueado hasta que la secretaria cancelara o reprogramara esa cita.

## Solución

Nuevo endpoint **`DELETE /citas/{id}/llamado`** (solo rol `MEDICO`, regla en `SecurityConfig` junto a la del `PATCH`):

- Borra `hora_solicitud_llamado` de la cita; la llegada del paciente **se conserva** (sigue presente en la cola).
- Rechaza (400) si la cita no es del médico del token ("El médico solo puede cancelar el llamado de pacientes de sus
  propias citas."), si no tiene llamado pendiente ("Esta cita no tiene un llamado pendiente.") o si no está "En espera".
- Publica `CITA_ACTUALIZADA` por WebSocket: el Inicio y el Llamador de la secretaria quitan el aviso al instante.

## Archivos modificados

```
config/SecurityConfig.java
controllers/clinico/CitaController.java
services/CitaService.java, services/impl/CitaServiceImpl.java (cancelarLlamado)
```

## Resultado (verificado)

Médico con un llamado: cancelarlo → 200 y `horaSolicitudLlamado` en null; luego llamar al siguiente → 200. Cancelar un llamado
inexistente → 400; que lo cancele otro médico → 400; la secretaria → 403.

## Consideraciones

- El paciente vuelve a la cola de "Presentes en Sala" y puede ser llamado de nuevo después.
- Pantalla: `frontend/documentacion/correcciones17.md`.
