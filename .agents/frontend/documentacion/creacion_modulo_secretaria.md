# Creación — Módulo Secretaria (Frontend)

## Objetivo

Implementar en Angular las pantallas de Secretaria propuestas en `vistas/secretaria/`,
respetando `reglas_estructura_y_features.md`: organización por funcionalidad (`pacientes`,
`citas`, `documentos`), conexión WebSocket única en `core` y control de acceso por rol.

## Archivos creados

```
frontend/src/app/
├── core/services/websocket.service.ts
├── shared/pipes/nombre-completo.pipe.ts, hora12.pipe.ts
└── features/
    ├── pacientes/   pacientes.routes.ts, models/, services/paciente.service.ts,
    │                components/paciente-form/, pages/listar/
    ├── citas/       citas.routes.ts, models/cita.model.ts, services/cita.service.ts,
    │                components/cita-form-modal/,
    │                pages/{agenda,inicio,llamador}/
    └── documentos/  documentos.routes.ts, models/, services/documento.service.ts,
                     components/{subir-documento-modal,reemplazar-documento-modal}/,
                     pages/listar/
```

## Archivos modificados

- `app.route.ts`: rutas de Secretaria bajo `authGuard` + `roleGuard` (`SECRETARIA`). `/inicio`,
  `/pacientes`, `/citas`, `/documentos` usan el layout principal; `/llamador` es pantalla completa
  fuera del layout (como en la vista propuesta).
- `layouts/main/main-layout.*`: el menú y el rótulo dependen del rol (admin o secretaria).
- `core/services/auth.service.ts`: ruta de inicio de `SECRETARIA` → `/inicio`.
- `scripts/generate-env.mjs`: los `environments/*.ts` no se editan a mano (ver `correcciones1.md`); el script ahora
  también genera `wsUrl` = `apiUrl` con `http`→`ws` / `https`→`wss` + `/ws/citas` (local:
  `ws://localhost:${SERVER_PORT}/ws/citas`; producción: derivada de `API_URL_PRODUCTION`). `WebSocketService`
  se conecta a `environment.wsUrl`.

## Funcionalidades

- **Pacientes** (`/pacientes`): directorio con búsqueda por DPI/nombre/teléfono, alta y edición en
  modal (el DPI no se edita), activar/desactivar. Enlaces a Programar Cita y Documentos con el
  paciente ya elegido (`?idPaciente=`).
- **Citas** (`/citas`): agenda diaria por fecha y médico; programar en modal (paciente, médico,
  fecha y horarios calculados por el Backend), reprogramar y cancelar (con confirmación).
- **Inicio de recepción** (`/inicio`): citas de hoy, "Marcar Llegada", banner de llamados
  solicitados por médicos y cola del día (presentes por orden de llegada, luego el resto por hora).
- **Llamador** (`/llamador`): a quién llamar, otros llamados y siguientes en espera.
- **Documentos** (`/documentos`): documentos del paciente, subir, ver (enlace firmado), reemplazar
  con motivo obligatorio y bitácora de auditoría.
- **Tiempo real**: `WebSocketService` (core) se conecta solo mientras hay sesión de SECRETARIA o
  MEDICO, autentica enviando el JWT como primer mensaje, reconecta cada 3 s y emite `CONECTADO`
  al reconectar. `CitaService.cambios$` lo expone a las páginas, que vuelven a consultar por REST.

## Integración

```
Page → Feature Service (HttpClient) → Spring Boot API → PostgreSQL / Supabase Storage
Page → CitaService.cambios$ → WebSocketService → /ws/citas
```

## Diferencias con las vistas propuestas (por el modelo de datos o el análisis)

- Sin columna "No. Expediente" (el expediente es una vista lógica, no un dato) y sin
  "Procedencia" del documento ni "Consultorio" (no existen en el modelo).
- Correo y dirección del paciente son obligatorios (columnas `NOT NULL`); en la vista eran opcionales.
- El motivo de reemplazo de un documento es un catálogo, no texto libre (así es el modelo).
- La bitácora muestra nombres de archivo (las rutas de Storage son identificadores internos).
- Los estados de cita son los tres del análisis; no existe "En atención médica".

## Consideraciones

- Los features no se importan entre sí: `documentos` y `citas` consultan `/pacientes` con su propio
  servicio y modelo mínimo. Los pipes compartidos se usan en 4 o más lugares.
- No se creó `notification.service.ts`: nada lo necesita todavía.
- Verificado con `ng build` (sin errores) y en navegador: login de secretaria, alta de paciente,
  programar cita, subir y reemplazar documento (con auditoría), actualización en vivo de llegada y
  llamado, y pantalla del llamador.
- Para usar el Llamador en pantalla de sala basta abrir `/llamador` con una sesión de SECRETARIA.
