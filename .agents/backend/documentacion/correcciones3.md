# Corrección 3 — Agenda de citas por rango de fechas (Backend)

## Problema
La agenda solo se podía consultar por un día (`GET /citas/agenda-diaria`), por lo que la vista
de agenda no podía mostrar un mes completo.

## Solución
Nuevo endpoint `GET /citas/agenda?desde=AAAA-MM-DD&hasta=AAAA-MM-DD` que devuelve las citas del
rango ordenadas por fecha y hora. Si `desde` es posterior a `hasta` responde 400. Los endpoints
existentes no cambian (Inicio y Llamador siguen usando `agenda-diaria`). La seguridad ya cubre
`GET /citas/**` (secretaria y médico).

## Archivos modificados
- `CitaRepository`: `findByFechaBetweenOrderByFechaAscHoraAsc`.
- `CitaService` / `CitaServiceImpl`: `obtenerAgendaPorRango`.
- `CitaController`: `GET /citas/agenda`.

## Resultado
Verificado: septiembre y octubre devuelven sus citas y un rango invertido responde 400.
Requiere reiniciar el backend.
