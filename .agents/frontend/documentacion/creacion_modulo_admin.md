# Creación — Módulo Administrador (Frontend)

## Objetivo

Inicializar la aplicación Angular (no existía `package.json` ni `angular.json` en el
repo, solo dos archivos vacíos) e implementar el módulo de Administrador consumiendo
la API REST del Backend, migrando el diseño visual de `vistas/admin/` y
`vistas/login.html` a componentes Angular standalone, respetando
`.agents/reglas_estructura_y_features.md`.

Angular CLI 22 (última versión disponible al momento de esta implementación) ya no
usa `NgModule` por defecto: toda la app es standalone (`bootstrapApplication`,
`provideRouter`, `provideHttpClient`), así que no existe `app.module.ts`.

## Archivos creados

```
frontend/
├── package.json, angular.json, tsconfig.json, tsconfig.app.json
├── public/favicon.ico, public/logo.png        (copiado desde la raíz del repo)
├── src/
│   ├── main.ts
│   ├── index.html
│   ├── styles.css                              (migrado de vistas/css/main.css + responsive.css)
│   ├── environments/environment.ts, environment.production.ts
│   └── app/
│       ├── app.component.ts                    (raíz, solo <router-outlet>)
│       ├── app.route.ts                         (rutas raíz: login / admin)
│       ├── app.config.ts                        (providers: router, http client)
│       ├── core/
│       │   ├── models/auth-response.model.ts, login-request.model.ts
│       │   └── services/auth.service.ts
│       ├── shared/components/loading-spinner/   (único componente reutilizable real)
│       ├── layouts/
│       │   ├── login/                           (migrado de vistas/login.html)
│       │   └── main/                            (sidebar + router-outlet, migrado de la estructura común de vistas/admin/*.html)
│       └── features/admin/
│           ├── admin.routes.ts
│           ├── models/ (catalogo, usuario, medico, jornada, dashboard)
│           ├── services/ (catalogo, usuario, especialidad, medico, jornada, dashboard)
│           └── pages/
│               ├── dashboard/
│               ├── usuarios/
│               ├── especialidades/
│               ├── medicos/
│               └── jornadas/
```

## Funcionalidades

- **Login (bypass intencional)**: `layouts/login/login.component.ts` reutiliza el
  diseño de `vistas/login.html` (logo, formulario). Al enviar el formulario,
  `ingresar()` navega directo a `/admin/dashboard` sin llamar al Backend ni validar
  nada, tal como se pidió para esta fase. `core/services/auth.service.ts` sí expone un
  `login()` real (`POST /auth/login`) para cuando se conecte la autenticación JWT, pero
  el formulario no lo invoca todavía.
- **Layout Admin**: `layouts/main/main-layout.component.ts` reproduce el sidebar común
  a todas las vistas de `vistas/admin/*.html` (marca, rol, navegación, perfil) con
  `routerLink`/`routerLinkActive`, y envuelve las 5 páginas vía `<router-outlet>`.
- **Dashboard** (`/admin/dashboard`): consume `GET /dashboard`, muestra las 3 métricas
  (pacientes, consultas realizadas, citas canceladas) y la tabla de consultas por
  médico.
- **Usuarios** (`/admin/usuarios`): listado + búsqueda cliente por nombre/correo,
  modal de alta/edición (formulario reactivo), activar/inactivar.
- **Especialidades** (`/admin/especialidades`): listado, modal de alta/edición,
  eliminación. Si el Backend responde 400 (especialidad con médicos asociados), se
  muestra el mensaje de la regla de negocio en vez de un error genérico.
- **Médicos** (`/admin/medicos`): listado, modal de alta (usuario vinculado +
  especialidad única + colegiado) y edición (especialidad + colegiado, el usuario no
  se puede reasignar), activar/inactivar reutilizando el estado del Usuario asociado.
- **Jornadas** (`/admin/jornadas`): selector de médico, tabla de períodos por día de la
  semana, modal de alta/edición/eliminación de períodos (permite turnos fragmentados
  registrando varias jornadas para el mismo día).

## Integración

```
*Component (features/admin/pages/*)
        │
        ▼
*Service (features/admin/services/*, HttpClient)
        │
        ▼
Spring Boot API (environment.apiUrl, default http://localhost:8080)
        │
        ▼
PostgreSQL (Docker)
```

`environment.ts` define `apiUrl` para desarrollo (`http://localhost:8080`);
`environment.production.ts` se sustituye en el build de producción vía
`fileReplacements` en `angular.json` — debe actualizarse con la URL real de Railway
antes de desplegar.

## Consideraciones

- **No se implementaron `auth.guard.ts` / `role.guard.ts` / `auth.interceptor.ts`.**
  `.agents/reglas_estructura_y_features.md` los describe como parte de `core/`, pero
  mientras el login siga en bypass y el Backend no emita JWT, un guard "protegiendo"
  rutas no tendría ninguna sesión real que verificar — se habría tenido que inventar
  un estado de autenticación falso. Se documenta como pendiente explícito para cuando
  se conecte JWT real (ver `.agents/backend/documentacion/creacion_modulo_admin.md`).
- Tampoco se implementó `websocket.service.ts` / `notification.service.ts`: ninguna
  pantalla del módulo Admin requiere actualización en tiempo real (son operaciones
  CRUD estándar); `.agents/reglas_implementacion.md` §13.10 pide usar WebSocket
  únicamente cuando aporte valor real.
- La tabla de especialidades no muestra "Médicos Asociados" ni "Descripción" como el
  mockup de `vistas/admin/especialidades.html`: `CatalogoResponseDTO` (lo que expone
  el Backend) solo tiene `id` y `nombre`; se prefirió no inventar esos datos en el
  Frontend antes que mostrar información falsa.
- Verificado con `ng build` (desarrollo y producción) sin errores. No se probó en
  navegador contra un Backend real porque este entorno no tiene Docker para levantar
  PostgreSQL (ver limitaciones en la documentación de Backend).

## Cómo ejecutar el frontend localmente

Requisitos: Node.js 20+ (usado aquí: Node 24) y el Backend corriendo en
`http://localhost:8080` (ver documentación de Backend).

```bash
cd frontend
npm install
npm start          # ng serve, http://localhost:4200
```
