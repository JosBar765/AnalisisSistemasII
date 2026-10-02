# Corrección 17 — Botón "Cancelar Llamado" en la agenda del médico (Frontend)

> Acompaña a `backend/documentacion/correcciones22.md`.

## Solución

En "Mi Agenda y Cola", el aviso amarillo de cada llamado pendiente ("Solicitud enviada a Recepción...") ahora tiene dos
botones: **Cancelar Llamado** (nuevo) e **Iniciar Consulta Médica**. Al cancelar, se llama a
`DELETE /citas/{id}/llamado`, la agenda se recarga y los botones "Solicitar Llamado" se habilitan de nuevo
(`correcciones16.md`). Un error del Backend sale en el aviso emergente.

## Archivos modificados

```
features/agenda-medica/services/agenda-medica.service.ts   (cancelarLlamado)
features/agenda-medica/pages/agenda/agenda.component.ts y .html
```

## Consideraciones

- Verificado con `ng build` y por API; el botón no se probó en el navegador.
- No pide confirmación: se puede volver a llamar al paciente de inmediato.
