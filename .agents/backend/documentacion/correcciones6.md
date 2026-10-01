# Corrección 6 — Errores del cliente respondían 500 (Backend)

## Problema

`GlobalExceptionHandler` solo tenía manejadores para las excepciones propias y de validación; todo lo demás
caía en `handleGeneric` y respondía **500** ("Ocurrió un error inesperado"), aunque la falla fuera del cliente:

- Cuerpo JSON mal formado o con tipos incorrectos (`HttpMessageNotReadableException`).
- Parámetro con formato inválido, por ejemplo `?fecha=abc` (`MethodArgumentTypeMismatchException`).
- Ruta inexistente (`NoResourceFoundException`).

Un 500 indica un error del servidor: confunde al frontend y a quien lea los logs.

## Solución

Se agregaron dos manejadores en `GlobalExceptionHandler`, sin cambiar los existentes:

| Excepción | Respuesta |
| --- | --- |
| `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException` | 400 "La solicitud tiene un formato inválido (cuerpo JSON o parámetro mal formado)." |
| `NoResourceFoundException` | 404 "El recurso solicitado no existe." |

El formato de la respuesta es el mismo `ErrorResponseDTO` de siempre.

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/exceptions/GlobalExceptionHandler.java`

## Resultado (verificado con el Backend en ejecución)

JSON cortado en `POST /pacientes` → 400; `GET /catalogos/nada` → 404; `?fecha=abc` → 400. Sin regresiones:
una consulta válida sigue en 200, un recurso de negocio inexistente sigue en 404 con su mensaje y una
petición sin token sigue en 401 (la seguridad responde antes de llegar al manejador). `mvnw compile` sin errores.

## Consideraciones

- Esto **no** cambia los 401/403 de Spring Security (se resuelven antes de los controllers).
- El mensaje 400 es genérico a propósito: no expone el detalle interno del parseo. El detalle queda en el
  log del servidor (nivel WARN, por `ExceptionHandlerExceptionResolver`).
- Un método HTTP no permitido (p. ej. `DELETE /pacientes`) respondía 500; **resuelto después** en
  `correcciones8.md` (405).
