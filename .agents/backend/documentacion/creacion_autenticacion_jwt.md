# Creación — Autenticación JWT (Backend)

## Objetivo

Reemplazar el login simulado (token UUID sin firma y endpoints abiertos) por autenticación
real con JWT y autorización por rol, según `.agents/reglas_despliegue.md` secciones 9 y 11.
Sustituye las limitaciones 1 y 2 de `creacion_modulo_admin.md`.

## Archivos

```
backend/pom.xml                                   (+ spring-boot-starter-security-oauth2-resource-server)
backend/src/main/resources/application.properties (+ app.jwt.secret, app.jwt.expiration-minutes)
config/JwtConfig.java                             (nuevo: JwtEncoder / JwtDecoder HS256)
security/JwtService.java                          (nuevo: genera el token)
config/SecurityConfig.java                        (modificado)
services/impl/AuthServiceImpl.java                (modificado: devuelve JWT real)
domain/dtos/auth/LoginRequestDTO.java             (validación @NotBlank)
domain/dtos/auth/AuthResponseDTO.java             (solo comentario)
controllers/auth/AuthController.java              (@Valid)
.env.example                                      (+ JWT_SECRET)
```

## Cambios realizados

- `POST /auth/login` valida credenciales (BCrypt, usuario activo) y devuelve un JWT HS256.
  Claims: `sub` (id de usuario), `rol`, `nombre`, `iat`, `exp`. Vigencia: 480 minutos
  (`app.jwt.expiration-minutes`).
- Se usa el soporte JWT de Spring Security (Nimbus, `oauth2ResourceServer`) en lugar de una
  librería externa, por lo que no hay filtro propio que mantener.
- `SecurityConfig`: `/auth/login` público; `/usuarios`, `/medicos`, `/especialidades`,
  `/jornadas`, `/dashboard` y `/categorias-documento` solo `ADMINISTRADOR`; el resto exige
  autenticación. El claim `rol` se convierte en la autoridad `ROLE_<rol>`.
- Respuestas: sin token o token inválido/vencido → 401; rol insuficiente → 403.

## Integración

```
Angular ── POST /auth/login ──► AuthController ─► AuthServiceImpl ─► JwtService ─► JWT
Angular ── Authorization: Bearer <JWT> ──► Spring Security (JwtDecoder) ─► Controller
```

## Consideraciones

- Nueva variable obligatoria `JWT_SECRET` (mínimo 32 caracteres). Debe configurarse también
  en Railway; no tiene valor por defecto a propósito.
- Al agregar los módulos de Secretaria y Médico hay que añadir sus reglas por rol en
  `SecurityConfig` (hoy solo exigen estar autenticado).
- Un token ya emitido sigue siendo válido hasta que vence aunque el usuario se inactive
  después; el estado solo se verifica al iniciar sesión.
- `SubirDocumentoRequestDTO.idUsuarioCarga` puede reemplazarse ya por el `sub` del token
  (pendiente, no incluido aquí).
- Probado contra PostgreSQL en Docker: login válido (200 + token), credenciales erróneas
  (401), campos vacíos (400), `/usuarios` sin token (401), con token de admin (200), con
  token falso (401), preflight CORS desde `http://localhost:4200`.
