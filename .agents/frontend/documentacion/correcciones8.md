# Corrección 8 — El modal se cerraba al soltar el mouse fuera del formulario (Frontend)

## Problema

Al presionar el botón del mouse dentro de un campo (por ejemplo para seleccionar texto) y soltarlo **fuera** del
formulario, el modal se cerraba y se perdía lo escrito.

## Causa

Todos los modales cerraban con `(click)` en `.modal-backdrop` (la tarjeta frenaba la propagación con
`(click)="$event.stopPropagation()"`). Cuando el `mousedown` ocurre en un elemento y el `mouseup` en otro, el
navegador dispara el `click` sobre el **ancestro común**, que aquí es el fondo; la tarjeta no lo ve y no puede
detenerlo, así que el modal se cerraba.

## Solución

Nueva directiva `shared/directives/fondo-modal.directive.ts` (`appFondoModal`), usada en el fondo de cada modal:
registra dónde empezó el `mousedown` y emite `fondoClick` solo si **el pulsado y el soltado fueron sobre el
propio fondo**. Reemplaza `(click)="cerrar()"` por `appFondoModal (fondoClick)="cerrar()"` en los 10 modales:
administración (usuarios, médicos, jornadas, especialidades), paciente-form, cita-form-modal,
confirmación de cancelar cita (`citas/agenda`), modificar-consulta-modal y subir/reemplazar documento. Se
extrajo a `shared` por usarse en más de 4 sitios.

## Archivos modificados

```
shared/directives/fondo-modal.directive.ts (nuevo)
.component.html y .component.ts de los 10 modales (importan la directiva)
```

## Consideraciones

- Un clic normal sobre el fondo sigue cerrando el modal; el botón "×" y "Cancelar" no cambian.
- Todo modal nuevo debe usar `appFondoModal` en su `.modal-backdrop` (no `(click)`).
- Verificado en el navegador: arrastrar desde un campo hasta fuera del modal ya no lo cierra, y un clic sobre el
  fondo sí lo cierra. `ng build` sin errores.
