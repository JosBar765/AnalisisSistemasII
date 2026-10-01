# Corrección 11 — El médico puede consultar su perfil y especialidad (Backend)

> Cierra la limitación de `creacion_modulo_medico.md` ("el médico no ve su propia especialidad").

## Problema

`GET /medicos/**` es solo de administrador y secretaria, y el JWT no incluye la especialidad, así que la interfaz
del médico no podía mostrarla.

## Solución

Nuevo endpoint `GET /medicos/me` (solo `MEDICO`) que devuelve el `MedicoResponseDTO` del médico autenticado
(`Medico.id` es el mismo `Usuario.id`, o sea el `sub` del JWT). Se prefirió un endpoint a agregar la especialidad
al JWT para que el dato esté siempre actualizado si el administrador la cambia.

`SecurityConfig`: `GET /medicos/me` → `MEDICO`, declarado **antes** de `GET /medicos/**` (administrador y
secretaria). El resto de reglas no cambia.

## Archivos modificados

- `controllers/admin/MedicoController.java` (`GET /me`)
- `config/SecurityConfig.java`

## Resultado (verificado)

Médico → 200 con `especialidadResponseDTO` (`Cardiologia`); secretaria y administrador → 403; `GET /medicos`
sigue en 200 para la secretaria.

## Consideraciones

- Un usuario con rol médico sin fila en `Medico` recibe 404 ("No se encontró el médico").
- Uso en la interfaz: `frontend/documentacion/correcciones5.md`.
