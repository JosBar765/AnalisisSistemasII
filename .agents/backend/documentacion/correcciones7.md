# Corrección 7 — CORS derivado de `FRONTEND_PORT` (Backend)

## Problema

`CORS_ALLOWED_ORIGINS` era obligatoria y en desarrollo debía coincidir a mano con el puerto del frontend.
Al hacer configurable el puerto del frontend (`FRONTEND_PORT`, ver `frontend/documentacion/correcciones2.md`)
habría dos valores que mantener sincronizados, y un desajuste hace que el navegador bloquee la API.

## Solución

`application.properties`:

```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:${FRONTEND_PORT:4200}}
```

- Si `CORS_ALLOWED_ORIGINS` está definida, se usa tal cual (producción: URL pública del frontend; admite
  varias separadas por coma).
- Si no, se permite `http://localhost:<FRONTEND_PORT>` (4200 si tampoco existe).
- Sigue alimentando tanto el CORS HTTP (`CorsConfig`) como los orígenes del WebSocket (`WebSocketConfig`).

Esto matiza `correcciones2.md` del backend, que decía que ninguna variable tenía valor por defecto:
`CORS_ALLOWED_ORIGINS` ahora sí lo tiene (solo local).

## Archivos modificados

- `backend/src/main/resources/application.properties`
- `.env.example` (comentario en `CORS_ALLOWED_ORIGINS`)

## Consideraciones

- El valor debe incluir el esquema: `http://localhost:4200`, **no** `localhost:4200` (Spring compara el
  origen textualmente y el navegador envía `http://...`).
- En producción definir `CORS_ALLOWED_ORIGINS` explícitamente: el valor por defecto apunta a `localhost`.
- Cambiar el `.env` requiere reiniciar el backend (se lee solo al arrancar).
- Verificado con un backend temporal sin `CORS_ALLOWED_ORIGINS` y `FRONTEND_PORT=4300`: el preflight desde
  `http://localhost:4300` respondió 200 y desde `http://localhost:4200` respondió 403.
