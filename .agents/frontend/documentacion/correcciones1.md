# Corrección 1 — Environments del Frontend generados desde `.env`

> Para agentes de IA: este documento **reemplaza lo dicho sobre `environment.ts` /
> `environment.production.ts` en `creacion_modulo_admin.md`** (que los describe como archivos
> escritos a mano con `apiUrl: 'http://localhost:8080'`). Hoy ya no se editan a mano.

## Problema

- `frontend/src/environments/environment.ts` tenía la URL de la API fija (`http://localhost:8080`),
  pero el backend corre en el puerto definido por `SERVER_PORT` en el `.env` de la raíz
  (en el `.env` actual del desarrollador: `6061`). Cada desarrollador con un puerto distinto
  tenía que editar código.
- `environment.production.ts` tenía un placeholder (`https://<backend-railway-url>`) que había
  que reemplazar a mano antes de desplegar.
- No existía una variable para distinguir la URL de la API local de la de producción.

## Solución

Angular no puede leer `.env` en tiempo de ejecución (las variables se "hornean" en el build).
Por eso se agregó un script que **genera** los dos archivos de environment antes de
`start`/`build`, leyendo la configuración del `.env` de la raíz del repo.

```
.env (raíz)  ─┐
              ├─► frontend/scripts/generate-env.mjs ─► src/environments/environment.ts
variables de  ─┘                                     └► src/environments/environment.production.ts
entorno (Railway)
```

### Origen de los valores

| Archivo generado             | `apiUrl`                                 | Variable de origen                         |
| ---------------------------- | ---------------------------------------- | ------------------------------------------ |
| `environment.ts` (desarrollo) | `http://localhost:${SERVER_PORT}`        | `SERVER_PORT` (obligatoria)                |
| `environment.production.ts`  | valor literal de la variable              | `API_URL_PRODUCTION` (nueva)               |

- Prioridad de lectura: si existe `../.env` se lee de ahí; las variables de entorno del proceso
  (`process.env`) son la base, y el `.env` las sobrescribe. En Railway no hay archivo `.env`, así
  que se usan las variables del servicio.
- `SERVER_PORT` ausente → el script falla con `Falta la variable SERVER_PORT ...`.
- `API_URL_PRODUCTION` es obligatoria **solo** con el flag `--production` (script `build:prod`).
  En los demás casos, si falta, `environment.production.ts` queda con `apiUrl: ''`.
- No se creó una variable para la URL local: se deriva de `SERVER_PORT`, que el backend ya usa
  (`server.port=${SERVER_PORT}` en `application.properties`), así frontend y backend comparten
  una sola fuente de verdad del puerto.

### Scripts de `frontend/package.json`

| Comando              | Qué hace                                                                     |
| -------------------- | ---------------------------------------------------------------------------- |
| `npm start`          | `prestart` genera environments → `ng serve`                                   |
| `npm run build`      | `prebuild` genera environments → `ng build` (config por defecto de Angular)  |
| `npm run watch`      | `prewatch` genera environments → `ng build --watch`                          |
| `npm run build:prod` | genera con `--production` (exige `API_URL_PRODUCTION`) → `ng build --configuration production` |

`ng build` con la configuración de producción sustituye `environment.ts` por
`environment.production.ts` (`fileReplacements` en `angular.json`, sin cambios).

## Archivos afectados

```
frontend/scripts/generate-env.mjs            (nuevo)
frontend/package.json                        (scripts prestart, prebuild, prewatch, build:prod)
frontend/src/environments/environment.ts            (ahora generado; ya no se versiona)
frontend/src/environments/environment.production.ts (ahora generado; ya no se versiona)
.env.example                                 (nueva variable API_URL_PRODUCTION)
.gitignore                                   (ignora frontend/src/environments/*.ts)
```

Los dos `environment*.ts` se quitaron del índice de git (`git rm --cached`) y están en
`.gitignore`. Siguen existiendo en disco tras generarse. Un clon nuevo no los trae: se crean al
ejecutar `npm start` o `npm run build`.

## Integración

- Los servicios del frontend siguen leyendo `environment.apiUrl` (p. ej. `AuthService` y
  `authInterceptor`, que solo agrega el `Bearer` a solicitudes hacia `environment.apiUrl`). **El
  código de la aplicación no cambió**; solo cambia cómo se produce el valor.
- El backend y su CORS no cambiaron. `CORS_ALLOWED_ORIGINS` sigue siendo configuración del
  backend (`.env`: `localhost:4200` en local).
- `.env.example` ahora contiene, en la sección de API: `SERVER_PORT`, `CORS_ALLOWED_ORIGINS`,
  `JWT_SECRET` y, al final, `API_URL_PRODUCTION`.

## Consideraciones

- **Nunca editar a mano** `environment.ts` ni `environment.production.ts`: se sobrescriben en
  cada `start`/`build`. Para cambiar la URL, modificar el `.env` (o las variables de Railway).
- **Railway (servicio Frontend):** definir `API_URL_PRODUCTION` (URL pública HTTPS del backend) y
  usar `npm run build:prod` como comando de build. Con `npm run build` a secas la URL de
  producción podría quedar vacía sin aviso.
- Cada desarrollador debe añadir `API_URL_PRODUCTION` a su `.env` local solo si va a probar el
  build de producción. No es necesaria para `npm start`.
- La URL queda embebida en el bundle JS, por lo que no debe contener secretos; `API_URL_PRODUCTION`
  es pública por naturaleza.
- El puerto `6061` citado arriba es el del `.env` local de un desarrollador, no un valor fijo del
  proyecto. `.env` no se versiona.
- `wsUrl` (WebSocket) también lo genera el script a partir de `apiUrl`; ver `creacion_modulo_secretaria.md`.
- Verificado: `npm run prestart` con el `.env` actual genera `apiUrl: 'http://localhost:6061'`.
  No se ejecutó `ng serve` ni `ng build` tras el cambio.


# Corrección 1 — Agenda de citas: filtro por mes (Frontend)

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
