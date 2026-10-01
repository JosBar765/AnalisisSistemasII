# Corrección 8 — Método HTTP no permitido respondía 500 (Backend)

> Cierra el pendiente anotado en `correcciones6.md`.

## Problema

Usar un método HTTP que el recurso no admite (p. ej. `DELETE /catalogos/roles`) lanzaba
`HttpRequestMethodNotSupportedException`, que caía en `handleGeneric` y respondía **500**.

## Solución

Nuevo manejador en `GlobalExceptionHandler`: `HttpRequestMethodNotSupportedException` → **405** con el mensaje
"El método HTTP <método> no está permitido para este recurso.", en el formato `ErrorResponseDTO` habitual.

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/exceptions/GlobalExceptionHandler.java`

## Resultado (verificado con el Backend en ejecución)

`DELETE /catalogos/roles` y `POST /catalogos/roles` → 405; `GET /catalogos/roles` sigue en 200.

## Consideraciones

- La seguridad se evalúa antes: sin token o sin rol suficiente se responde 401/403 y no 405.
- Un tipo de contenido no soportado (`HttpMediaTypeNotSupportedException`, 415) sigue sin manejador propio.
