# Corrección 2 — Puerto del frontend configurable desde `.env` (Frontend)

> Complementa `correcciones1.md` (environments generados desde `.env`). La corrección de la agenda
> mensual, que antes estaba duplicada dentro de `correcciones1.md`, pasó a `correcciones3.md`.

## Problema

`npm start` levantaba `ng serve` siempre en el puerto 4200. Cambiarlo obligaba a pasar `--port` a mano,
y el backend (CORS) tenía el origen `http://localhost:4200` escrito en el `.env`: al cambiar el puerto del
frontend el navegador bloqueaba la API y el login mostraba "No se pudo conectar con el servidor".

## Solución

- Nueva variable `FRONTEND_PORT` en el `.env` (4200 si se omite).
- `frontend/scripts/start.mjs` lee el `.env` y ejecuta `ng serve --port <FRONTEND_PORT>`. `npm start` ahora
  es `node scripts/start.mjs` (el `prestart` que genera los environments sigue ejecutándose antes).
- La lectura del `.env` se movió a `frontend/scripts/cargar-env.mjs`, compartido por `generate-env.mjs` y
  `start.mjs` (antes estaba dentro de `generate-env.mjs`). Reglas de lectura sin cambios: las variables del
  proceso son la base y el `.env` las sobrescribe.
- El backend deriva su CORS del mismo valor (ver `backend/documentacion/correcciones7.md`), así que no hay
  que editar dos variables al cambiar el puerto.

## Archivos modificados

```
frontend/scripts/cargar-env.mjs     (nuevo: lectura de .env y `requerida`)
frontend/scripts/start.mjs          (nuevo: ng serve con FRONTEND_PORT)
frontend/scripts/generate-env.mjs   (usa cargar-env.mjs; sin cambios de comportamiento)
frontend/package.json               (script start)
.env.example                        (reordenado por secciones; nueva FRONTEND_PORT)
```

`.env.example` quedó en este orden: Base de datos → API (desarrollo) → API en producción → Frontend →
Supabase Storage.

## Consideraciones

- Argumentos extra se pasan a `ng serve`: `npm start -- --open`.
- `FRONTEND_PORT` solo afecta al servidor de desarrollo. En Railway el frontend usa el puerto que asigne la
  plataforma y `CORS_ALLOWED_ORIGINS` debe ser la URL pública del frontend.
- Como el `.env` **sobrescribe** las variables del proceso, `FRONTEND_PORT=4300 npm start` no tiene efecto si el
  `.env` ya define `FRONTEND_PORT`; hay que cambiar el `.env`.
- Verificado: con `FRONTEND_PORT=4300` en el `.env`, `ng serve` escucha en 4300 y no en 4200.
