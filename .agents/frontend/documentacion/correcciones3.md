# Corrección 3 — Agenda de citas: filtro por mes (Frontend)

## Problema
La agenda de citas solo mostraba las citas de un día.

## Solución
En `/citas` se agregaron dos botones de vista **Día / Mes**. En modo Mes se muestra un navegador con
el mes y año (por ejemplo "Octubre 2026") y dos botones: `<` retrocede un mes y `>` avanza un mes.
La tabla agrega la columna Fecha, el filtro por médico sigue aplicando y las actualizaciones en
tiempo real recargan el mes consultado. El título de la tarjeta cambia a "Agenda Mensual".

## Archivos modificados
- `features/citas/services/cita.service.ts`: `listarAgendaRango`.
- `features/citas/pages/agenda/agenda.component.ts` y `.html`.

## Resultado
Verificado en navegador: navegar de septiembre a octubre carga las citas de cada mes.
Depende del endpoint `GET /citas/agenda` (ver backend `correcciones3.md`).
