# Creación — Módulo Secretaria (Backend)

## Objetivo

Implementar la API de los casos de uso de Secretaria del análisis: UC-SEC-001 (pacientes),
UC-SEC-002 (documentos clínicos) y UC-SEC-003 (citas), más la comunicación en tiempo real de
citas (WebSocket) y el llamado de pacientes que solicita el médico. Antes de este cambio esos
endpoints existían como CRUD mínimo sin reglas de negocio (ver `creacion_modulo_admin.md`,
sección "Fuera de alcance").

## Cambios aprobados fuera del análisis / modelo original

El usuario aprobó explícitamente:

1. **Cambio de esquema** en `Cita` (`database/schema.sql`; antes `.agents/MediSistema.sql`): dos columnas nulables
   - `hora_llegada time`: la secretaria registra que el paciente está presente.
   - `hora_solicitud_llamado time`: el médico solicita a la secretaria llamar al paciente.
   El análisis dice que no hay un proceso de *check-in* independiente y que la secretaria
   determina quién está presente; estas columnas solo dan persistencia a esa información.
   Para bases ya creadas hay que ejecutar:
   ```sql
   ALTER TABLE "Cita" ADD COLUMN IF NOT EXISTS hora_llegada time,
                      ADD COLUMN IF NOT EXISTS hora_solicitud_llamado time;
   ```
2. **WebSocket** para eventos de citas, y la vista **Llamador** que depende de él.
3. **Supabase Storage** para los archivos (según `reglas_despliegue.md`).

## Archivos creados

```
backend/src/main/java/com/josbar/medisistemas/
├── config/CitaWebSocketHandler.java      canal /ws/citas: autenticación JWT + difusión de eventos
├── config/WebSocketConfig.java           registro del handler y orígenes permitidos
├── services/CitaEventoPublisher.java     abstracción con la que CitaService publica eventos
├── services/AlmacenamientoService.java   guardar archivo / generar enlace temporal
├── services/impl/SupabaseAlmacenamientoServiceImpl.java
├── exceptions/AlmacenamientoException.java
├── domain/dtos/cita/EventoCitaDTO.java, TipoEventoCita.java
└── domain/dtos/documento/EnlaceDocumentoResponseDTO.java
```

## Archivos modificados

- `pom.xml`: `spring-boot-starter-websocket`.
- `application.properties`, `.env.example`: subida multipart (20 MB) y variables
  `SUPABASE_URL`, `SUPABASE_SERVICE_KEY`, `SUPABASE_BUCKET` (por defecto vacías: sin ellas la
  API arranca, pero subir o leer documentos responde 503).
- `SecurityConfig`: reglas por rol (ver "Seguridad").
- `CitaEntity`, `CitaResponseDTO`, `CitaMapper`, `CitaRequestDTO`: nuevos campos y validación.
- `CitaService`/`CitaServiceImpl`/`CitaController`, `CitaRepository`.
- `PacienteRequestDTO`, `PacienteMapper`, `PacienteRepository`, `PacienteServiceImpl`,
  `PacienteController`.
- `DocumentoService`/`Impl`/`Controller`, `DocumentoMapper`, `SubirDocumentoRequestDTO`,
  `ActualizarDocumentoRequestDTO`.
- `AuditoriaService`/`Impl`, `AuditoriaController`, `AuditoriaDocumentoRepository`,
  `AuditoriaDocumentoResponseDTO`, `AuditoriaDocumentoMapper`.
- `GlobalExceptionHandler`: `AlmacenamientoException` → 503; falta de archivo o parámetro → 400
  (antes devolvía 500).

## Funcionalidades

**Pacientes (UC-SEC-001)**: `POST/GET/PUT /pacientes`, `GET /pacientes/buscar?dpi=`,
`PATCH /pacientes/{id}/estado`. Validación en el DTO. DPI único (400 si existe). Se corrigió un
defecto previo: el mapper no copiaba `correo` (columna `NOT NULL`, el alta habría fallado) ni
actualizaba `fechaNacimiento`. El DPI no se modifica.

**Citas (UC-SEC-003)**: `POST /citas`, `GET /citas/disponibilidad`, `GET /citas/agenda-diaria`,
`GET /citas/agenda-medico`, `PUT /citas/{id}/reprogramar`, `PUT /citas/{id}/cancelar`, y nuevos
`PATCH /citas/{id}/llegada` (secretaria) y `PATCH /citas/{id}/llamado` (médico). Reglas:
- Programar exige paciente activo, médico activo y un horario que esté entre los disponibles
  (jornada + duración + citas no canceladas). El estado inicial es "En espera".
- Reprogramar/cancelar solo sobre citas "En espera". Reprogramar valida el nuevo horario
  (excluyendo la propia cita), conserva médico y paciente, y limpia llegada y llamado.
- Llegada: solo citas de hoy, una sola vez. Llamado: solo el médico dueño de la cita y solo si el
  paciente ya llegó (el análisis: el médico decide entre los pacientes presentes).
- La hora sigue siendo estimada; el orden real de cola lo presenta el frontend según la llegada.

**Documentos (UC-SEC-002)**: `POST /documentos` (multipart: `archivo`, `idPaciente`,
`idCategoriaDocumento`), `PUT /documentos/{id}` (multipart: `archivo`, `idMotivoModificacion`,
`idCategoriaDocumento` opcional), `GET /documentos/paciente/{id}`, `GET /documentos/{id}/enlace`
(URL firmada de 5 minutos), `GET /auditorias/documentos/paciente/{id}`.
- El usuario que carga sale del JWT (`sub`); se eliminó el parche temporal `idUsuarioCarga`.
- Formatos permitidos: pdf, jpg, jpeg, png (`dcm` se retiró: `correcciones17.md`). Ruta en Storage:
  `paciente-{id}/{uuid}-{nombre}`; la columna `Documento.url` guarda esa ruta.
- Al reemplazar se crea `AuditoriaDocumento` (nombre/URL anterior y nueva, usuario, fecha,
  motivo). El archivo anterior **no** se borra del almacenamiento.
- El motivo es el catálogo `MotivoModificacionDocumento` (el modelo no tiene texto libre; la vista
  proponía un textarea).

**Tiempo real**: el handler recibe como primer mensaje el JWT (no viaja en la URL). Responde
`AUTH_OK` o cierra con 1008. Eventos `{"tipo","citaId","medicoId"}` (`CITA_CREADA`,
`CITA_ACTUALIZADA`, `CITA_CANCELADA`); `CITA_ATENDIDA` queda definido para el módulo Médico. La
secretaria recibe todos; un médico solo los de sus citas. Se publican tras el *commit* de la
transacción. El cliente consulta el detalle por REST.

## Seguridad (`SecurityConfig`)

> Actualizado por el módulo Médico (`creacion_modulo_medico.md`): el médico **ya no** lee
> `GET /citas/**` general, solo `GET /citas/mis-citas/**`; y `/consultas`, `/expedientes` y
> `/auditorias/consultas` son solo del médico. Lo siguiente describe la versión original de Secretaria.

- `GET /pacientes|documentos|auditorias/documentos`: SECRETARIA y MEDICO (y `GET /citas/**`
  solo SECRETARIA tras el módulo Médico).
- Resto de métodos sobre `/pacientes`, `/citas`, `/documentos`: solo SECRETARIA.
- `PATCH /citas/*/llamado`: solo MEDICO.
- `GET /medicos/**`: ADMINISTRADOR y SECRETARIA (necesaria para elegir médico al programar).
- `/ws/**` público en el handshake; la autenticación ocurre en el primer mensaje.

## Integración

```
Controller → Service → Repository → PostgreSQL
                │
                ├── AlmacenamientoService → Supabase Storage (REST, service key)
                └── CitaEventoPublisher   → CitaWebSocketHandler → clientes Angular
```

## Consideraciones

- Verificado con una batería de 52 pruebas de API (seguridad por rol, reglas de negocio,
  WebSocket, documentos) contra PostgreSQL en Docker, y con el navegador. **Supabase Storage se
  probó en esta fase contra un servidor simulado** (`POST /storage/v1/object/{bucket}/{ruta}` y
  `.../object/sign/...`); la verificación contra Supabase real se hizo después (ver
  `correcciones5.md`). El bucket debe existir y ser privado.
- No se valida que la fecha de una cita sea futura (el análisis no lo define).
- Reinicie el backend tras actualizar; el esquema requiere el `ALTER TABLE` indicado arriba.

### Pendientes que ya se resolvieron en otros documentos

- Archivo huérfano en Storage si falla el guardado en BD → `correcciones5.md` (se elimina el
  archivo nuevo si la transacción no se confirma; el barrido para el caso de caída del proceso está en `correcciones9.md`).
- Conexiones WebSocket sin autenticar sin cierre por tiempo → `correcciones5.md` (cierre a los 10 s).
- `CITA_ATENDIDA` y pasar la cita a "Atendido" → módulo Médico (`creacion_modulo_medico.md`).
- Errores del cliente que respondían 500 (JSON mal formado, parámetro inválido, ruta inexistente)
  → `correcciones6.md`.
- Los orígenes CORS/WebSocket permitidos ahora se derivan de `FRONTEND_PORT` → `correcciones7.md`.
