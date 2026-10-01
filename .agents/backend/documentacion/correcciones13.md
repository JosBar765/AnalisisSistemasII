# Corrección 13 — Jornadas médicas que se traslapaban (Backend)

> Cierra el pendiente de `correcciones12.md` ("evitar jornadas que se traslapan").

## Problema

`JornadaMedicaServiceImpl` solo validaba que la hora de inicio fuera anterior a la de fin y que la duración fuera
positiva. Un médico podía tener varios períodos que se pisaran el mismo día (08:00-12:00 y 10:00-13:00), y
`CitaServiceImpl.calcularHorariosDisponibles` generaba entonces horarios duplicados o inconsistentes. La base de
datos tampoco lo impide (no hay restricción de exclusión).

## Solución

`validarSinTraslape` en `JornadaMedicaServiceImpl`, invocado al **crear** y al **modificar**:

- Compara con las jornadas del mismo médico y el mismo día (`findByMedicoEntityIdAndDiaSemanaEntityId`), excluyendo
  la propia jornada al modificar.
- Hay traslape si `nuevo.inicio < existente.fin` y `nuevo.fin > existente.inicio`. Que un período termine justo
  cuando empieza otro **no** es traslape (10:00-13:00 y 13:00-17:00 es válido; es el caso de descanso del análisis).
- Responde 400 explicando con cuál choca: "El período 10:00 - 13:00 se traslapa con otro ya configurado para ese
  día (08:00 - 12:00)."

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/services/impl/JornadaMedicaServiceImpl.java`

## Resultado (verificado con el Backend en ejecución)

Con 08:00-12:00 creado: 10:00-13:00, 07:00-08:30 y 09:00-11:00 → 400; 12:00-16:00 (contigua) y 08:00-12:00 en otro
día → 201. Ampliar 08:00-12:00 a 08:00-13:00 cuando existe 12:00-16:00 → 400; cambiar solo la duración de una
jornada → 200.

## Consideraciones

- Es una regla de servicio; no hay restricción en la base. Un `INSERT` directo en SQL puede traslapar jornadas.
  Si se quisiera a nivel de base: restricción `EXCLUDE USING gist` (requiere la extensión `btree_gist`).
- Las jornadas que ya estuvieran traslapadas antes de este cambio no se corrigen ni se rechazan hasta que se
  modifiquen.
