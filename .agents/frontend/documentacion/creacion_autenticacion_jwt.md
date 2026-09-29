# Creación — Autenticación JWT (Frontend)

## Objetivo

Conectar el login con la API real y proteger las rutas, eliminando el bypass descrito en
`creacion_modulo_admin.md`. Cumple lo pendiente de `core/` en
`.agents/reglas_estructura_y_features.md`.

## Archivos

```
core/models/usuario-auth.model.ts       (nuevo: Rol, UsuarioAuth)
core/services/auth.service.ts           (login real, sesión, logout)
core/interceptors/auth.interceptor.ts   (nuevo)
core/guards/auth.guard.ts               (nuevo)
core/guards/role.guard.ts               (nuevo)
app.config.ts                           (registra el interceptor)
app.route.ts                            (ruta /admin con authGuard + roleGuard)
layouts/login/login.component.*         (formulario reactivo, errores, sin credenciales precargadas)
layouts/main/main-layout.component.*    (nombre/rol reales y cierre de sesión)
```

## Cambios realizados

- `AuthService` guarda el token en `localStorage`, lo decodifica (id, nombre, rol, exp) y expone
  `usuario` (signal), `estaAutenticado()`, `tieneRol()`, `rutaInicio()` y `logout()`. Un token
  vencido se descarta al cargar la app.
- `authInterceptor` agrega `Authorization: Bearer` a las solicitudes hacia `environment.apiUrl`
  y cierra la sesión si la API responde 401 (salvo en el propio login).
- `authGuard` exige sesión vigente; `roleGuard` valida `data.roles` de la ruta. Ambos redirigen a
  `/login`. Son solo experiencia de usuario: la autorización real es del Backend.
- Login: muestra el mensaje del Backend ante credenciales inválidas o usuario inactivo, y
  redirige según el rol.

## Integración

```
LoginComponent ─► AuthService ─► POST /auth/login ─► JWT en localStorage
authInterceptor ─► Bearer en cada llamada ─► Spring Security
```

## Consideraciones

- `RUTA_INICIO` en `auth.service.ts` solo tiene `ADMINISTRADOR`. Secretaria y Médico reciben el
  mensaje "aún no tiene un módulo disponible" hasta que existan sus features; al crearlos, agregar
  su ruta ahí y su `roleGuard` en `app.route.ts`.
- El token en `localStorage` es la opción más simple; queda expuesto ante XSS. Alternativa futura:
  cookie HttpOnly.
- No se implementó `WebSocketService`; sigue sin haber pantalla que lo requiera.
- Verificado con `ng build`. No se probó la interfaz en un navegador; el flujo HTTP sí se probó
  contra el Backend real (ver documentación de Backend).
