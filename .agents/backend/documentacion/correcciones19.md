# Corrección 19 — El historial de citas canceladas no baja al reprogramar (Backend y base de datos)

## Problema

El dashboard contaba "citas canceladas" como las citas cuyo estado actual es "Cancelado". Desde `correcciones18.md` una
cancelada puede reprogramarse y vuelve a "En espera", así que el contador **bajaba** y se perdía el historial.

## Cambio de esquema (aprobado por el usuario al pedir la regla)

Nueva columna en `Cita` (`database/schema.sql`):

```sql
"veces_cancelada" integer NOT NULL DEFAULT 0
```

Cuenta cuántas veces se canceló la cita; **nunca disminuye**. Para bases ya creadas:

```sql
ALTER TABLE "Cita" ADD COLUMN IF NOT EXISTS veces_cancelada integer NOT NULL DEFAULT 0;
UPDATE "Cita" SET veces_cancelada = 1
 WHERE id_estado_cita = (SELECT id FROM "EstadoCita" WHERE estado_cita = 'Cancelado');
```
(El `UPDATE` hace que las que hoy están canceladas cuenten una vez. Ya aplicado en la base local.)

## Solución

- `CitaEntity.vecesCancelada` (`@Builder.Default` en 0).
- `CitaServiceImpl.cancelar` suma 1 en cada cancelación.
- `CitaRepository.totalCancelaciones()` (`SUM(vecesCancelada)`) alimenta `DashboardServiceImpl`, que ya no cuenta por
  estado actual.

## Archivos modificados

```
database/schema.sql (+ columna y comentario)
domain/entities/CitaEntity.java, repositories/CitaRepository.java
services/impl/CitaServiceImpl.java, services/impl/DashboardServiceImpl.java
```

## Resultado (verificado)

Dashboard en 0 → cancelar una cita → 1 → reprogramar esa cancelada (vuelve a "En espera") → sigue en **1**. Cancelarla
otra vez sumaría 2.

## Consideraciones

- El dashboard cuenta **cancelaciones**, no citas distintas: una cita cancelada, reprogramada y cancelada otra vez cuenta 2.
- El número de citas "Cancelado" en la agenda sigue siendo por estado actual; solo el dashboard usa el historial.
