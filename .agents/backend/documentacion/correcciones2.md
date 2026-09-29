# Corrección 2 — Host de la base de datos parametrizado

## Problema

La URL JDBC tenía `localhost` escrito directamente en `application.properties`, lo que
impedía apuntar a otra base (por ejemplo Supabase) sin modificar código.

## Solución

- Se agregó la variable `DB_HOST` y la URL pasó a
  `jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}`.
- Se evaluó hacer obligatorio el `.env` (quitando `optional:` de `spring.config.import`),
  pero se descartó: en Railway las variables llegan como variables de entorno y no
  existe el archivo. Se mantiene `optional:file:../.env[.properties]`.
- Ninguna variable tiene valor por defecto, así que si falta alguna (no existe el `.env`
  ni la variable de entorno), Spring falla al arrancar con
  `Could not resolve placeholder '<VARIABLE>'`.

## Archivos modificados

- `backend/src/main/resources/application.properties`
- `.env.example` (nueva clave `DB_HOST`)
- `.env` local (se agregó `DB_HOST=localhost`; no se versiona)

## Consideraciones

- Cada desarrollador debe agregar `DB_HOST` a su `.env` local.
- En Railway se configuran las mismas variables del `.env.example` como variables del
  servicio; no se necesita el archivo.
