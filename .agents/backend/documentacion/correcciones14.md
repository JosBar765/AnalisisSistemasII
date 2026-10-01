# Corrección 14 — Solo un usuario con rol MEDICO puede ser médico (Servicio y Base de datos) (Backend)

> Cierra el pendiente de `correcciones12.md` ("exigir que el usuario del médico tenga rol `MEDICO`").

## Problema

Nada impedía registrar como médico a un usuario con rol SECRETARIA o ADMINISTRADOR, ni cambiarle el rol a un
usuario que ya era médico. Un médico con rol de secretaria no podría iniciar sesión como médico (el JWT lleva el rol).

## Solución

Se controla en **dos niveles** (decisión del usuario: no solo en el servicio):

1. **Servicio**
   - `MedicoServiceImpl.save` rechaza (400) si el usuario no tiene rol `MEDICO`: "El usuario debe tener el rol MEDICO
     para registrarse como médico (rol actual: SECRETARIA)."
   - `UsuarioServiceImpl.modificar` rechaza (400) cambiar el rol de quien ya está registrado como médico.
   - Nueva clase `security/Roles` con los nombres de los roles (`SecurityConfig` ahora la usa).
2. **Base de datos** (`database/schema.sql`, al final): dos *triggers* PL/pgSQL que lanzan un error de código
   `23514` (`check_violation`) con mensaje en español:
   - `trg_medico_requiere_rol_medico` (`BEFORE INSERT OR UPDATE ON "Medico"`): el usuario debe tener el rol `MEDICO`.
   - `trg_usuario_medico_conserva_rol` (`BEFORE UPDATE OF id_rol ON "Usuario"`): si el usuario es médico, el nuevo rol
     debe seguir siendo `MEDICO`.
   `MensajeIntegridadDatos` traduce el `23514` mostrando el mensaje del trigger tal cual (400), de modo que si algo
   evade el servicio (o hay una condición de carrera) el usuario ve la misma explicación.

Además se corrigió `UsuarioMapper.updateEntity`: al cambiar el rol mutaba el `id` de la entidad `Rol` gestionada
(`entity.getRolEntity().setId(...)`), lo que Hibernate no admite; ahora reemplaza la referencia por un `Rol` con el
nuevo id.

## Archivos modificados

```
database/schema.sql                                     (2 funciones + 2 triggers)
services/impl/MedicoServiceImpl.java, UsuarioServiceImpl.java
mappers/impl/UsuarioMapper.java
security/Roles.java (nuevo), config/SecurityConfig.java
exceptions/MensajeIntegridadDatos.java                  (+ 23514)
```

## Base de datos existente

Ejecutar en bases ya creadas el bloque final de `database/schema.sql` (desde "-- Un usuario solo puede registrarse
como médico..."). Una base nueva ya lo trae.

## Resultado (verificado)

Servicio: `POST /medicos` con un usuario SECRETARIA → 400 con el rol actual; cambiar el rol de un médico a SECRETARIA
→ 400; el mismo rol u otros campos → 200. Base: `INSERT INTO "Medico"` de un usuario SECRETARIA y
`UPDATE "Usuario" SET id_rol` de un médico fallan con `ERROR: 23514` y el mensaje en español.

## Consideraciones

- Un médico que ya existiera con un rol distinto de MEDICO antes de aplicar los triggers no se detecta hasta que se
  modifique su fila.
- El trigger compara por el **nombre** del rol (`'MEDICO'`), no por su id.
