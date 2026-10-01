# Corrección 4 — Cierre de sesión cuando el Backend la invalida (Frontend)

> Acompaña a `backend/documentacion/correcciones10.md`.

## Problema

Al desactivar a un usuario, el Backend cierra su WebSocket con código 1008. `WebSocketService` trataba cualquier
cierre como una caída de red y reintentaba cada 3 s; cada intento era rechazado de nuevo: un bucle infinito con la
sesión aparentemente abierta.

## Solución

`WebSocketService.onclose`: si el código es **1008** (`POLITICA_VIOLADA`) y el cierre no fue intencional, llama a
`AuthService.logout()` (borra el token y va a `/login`) en lugar de reintentar. Los demás cierres siguen
reintentando como antes. Las peticiones REST ya cerraban sesión ante un 401 (`authInterceptor`), que es lo que
devuelve el Backend con el token de un usuario inactivo.

## Archivos modificados

- `frontend/src/app/core/services/websocket.service.ts`

## Consideraciones

- 1008 también se usa para un token inválido o vencido al reconectar: en ambos casos la sesión ya no sirve.
- Los roles sin WebSocket (administrador) cierran sesión en su siguiente petición, por el 401.
- Verificado con `ng build`; el comportamiento del cierre 1008 se verificó contra el Backend (no se ejecutó la
  interfaz con un usuario desactivado en el navegador).
