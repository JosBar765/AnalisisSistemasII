# Corrección 10 — La sesión de un usuario desactivado deja de ser válida (Backend)

> Cierra la limitación de `creacion_autenticacion_jwt.md` ("un token sigue siendo válido hasta que vence aunque
> el usuario se inactive").

## Problema

El estado del usuario solo se verificaba en el login. Un usuario desactivado conservaba su JWT (hasta 8 h) y su
conexión WebSocket abierta, y podía seguir usando la API.

## Solución

- **REST.** `SecurityConfig` envuelve el conversor del JWT: tras validar la firma y el vencimiento consulta
  `SesionUsuarioValidator.estaActivo(sub)` (`UsuarioRepository.existsByIdAndEstadoTrue`). Si el usuario está
  inactivo **o ya no existe**, lanza `InvalidBearerTokenException` → **401**. Cuesta una consulta por petición.
- **WebSocket.** `CitaWebSocketHandler` aplica la misma validación al autenticar (primer mensaje) y cierra con
  `1008 "Sesión finalizada"` si el usuario no está activo.
- **Cierre inmediato.** Nueva abstracción `CierreSesionPublisher.cerrarSesionesDe(idUsuario)`, implementada por
  `CitaWebSocketHandler` (misma idea que `CitaEventoPublisher`). `UsuarioServiceImpl.cambiarEstado` y `modificar`
  la invocan cuando el usuario queda inactivo; el cierre se ejecuta **tras el commit** de la transacción.
- Reactivar al usuario hace que su token (si no venció) vuelva a aceptarse; no hay lista de tokens revocados.

## Archivos modificados

```
security/SesionUsuarioValidator.java        (nuevo)
services/CierreSesionPublisher.java         (nuevo)
config/SecurityConfig.java                  (conversor que valida al usuario)
config/CitaWebSocketHandler.java            (valida al autenticar; cerrarSesionesDe; despuesDelCommit)
services/impl/UsuarioServiceImpl.java       (cierra sesiones al desactivar)
repositories/UsuarioRepository.java         (existsByIdAndEstadoTrue)
```

## Resultado (verificado con el Backend en ejecución)

Con un médico de prueba y una secretaria conectados: antes de desactivar, REST 200 y ambos WebSocket abiertos.
Tras `PATCH /usuarios/{id}/estado?estado=false`: REST del médico → **401**, su WebSocket cerrado con
`1008 "Sesión finalizada"`, la reconexión con el mismo token cerrada igual, y la secretaria **no** se afectó. Al
reactivar, REST → 200. También se comprobó que el token de un usuario inexistente responde 401.

## Consideraciones

- El frontend reacciona: cierra sesión ante el 1008 y ante cualquier 401 (`frontend/documentacion/correcciones4.md`).
  Un usuario sin WebSocket (administrador) se entera en su siguiente petición.
- Un cambio de **rol** sigue sin invalidar el token (su claim `rol` queda vigente hasta que venza).
- Efecto colateral: bases recreadas dejan inválidos los tokens de usuarios que ya no existen.
