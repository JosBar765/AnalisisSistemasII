# CORRECCIONES AL ANÁLISIS — 1

Cambios de reglas de negocio decididos por el usuario durante el desarrollo. Complementan y, donde se indica,
reemplazan lo dicho en `analisis_medisistema.md`.

## UC-SEC-003 — Gestionar citas

### Citas del mismo paciente el mismo día

Un paciente **puede** tener varias citas el mismo día, solo si son con **médicos distintos y a horas distintas**.
Ejemplo: cardiólogo a las 11:00 y gastroenterólogo a las 12:30. No se permite una segunda cita el mismo día con el
mismo médico ni dos citas a la misma hora. Las citas canceladas no cuentan.
(Implementación: `backend/documentacion/correcciones16.md`.)

### Reprogramar citas

**Reemplaza** el flujo alternativo de la sección 9 ("En espera → Cancelación → Cancelado" como estado final):
una cita **Cancelada** se puede reprogramar y, al hacerlo, vuelve a **En espera**. Una cita Atendida no se puede
cancelar; reprogramarla **crea una cita nueva** (seguimiento) y conserva la atendida con su consulta.
El dashboard cuenta el historial de cancelaciones, que no baja al reprogramar.

```
Programar → En espera ⇄ Cancelado
            En espera → Atendido → (reprogramar) nueva cita En espera
```
(Implementación: `backend/documentacion/correcciones18.md`, `19`, `20` y `frontend/documentacion/correcciones14.md`, `15`.)

## UC-MED-001 — Consultar agenda médica

### Un solo llamado a la vez

El médico solo puede tener **un llamado a sala pendiente**: hasta que el paciente llamado sea atendido (o su cita se
cancele o reprograme) no puede solicitar otro.
(Implementación: `backend/documentacion/correcciones21.md`, `frontend/documentacion/correcciones16.md`.)
