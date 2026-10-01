# Creación — Módulo Médico (Backend)

## Objetivo

Implementar la API de los casos de uso del Médico del análisis: UC-MED-001 (agenda y cola),
UC-MED-002 (expediente clínico), UC-MED-003 (registrar consulta) y UC-MED-004 (modificar consulta con
auditoría), y su sinergia con el módulo de Secretaria (`creacion_modulo_secretaria.md`). Antes de este
cambio `ConsultaServiceImpl` era un CRUD mínimo sin validaciones ni auditoría (ver
`creacion_modulo_admin.md`, "Fuera de alcance").

## Decisiones aprobadas por el usuario (fuera del modelo original)

1. **Cambio de esquema** en `AuditoriaConsulta` (`database/schema.sql`): 10 columnas nulables para
   auditar los signos vitales, porque UC-MED-004 permite modificarlos y la tabla no podía guardarlos.
   `peso_anterior`, `altura_anterior`, `presion_sistolica_anterior`, `presion_diastolica_anterior`,
   `temperatura_anterior` y las equivalentes `*_nuevo/_nueva`. Para bases ya creadas:
   ```sql
   ALTER TABLE "AuditoriaConsulta"
     ADD COLUMN IF NOT EXISTS peso_anterior decimal(5,2), ADD COLUMN IF NOT EXISTS altura_anterior decimal(5,2),
     ADD COLUMN IF NOT EXISTS presion_sistolica_anterior integer, ADD COLUMN IF NOT EXISTS presion_diastolica_anterior integer,
     ADD COLUMN IF NOT EXISTS temperatura_anterior decimal(3,1), ADD COLUMN IF NOT EXISTS peso_nuevo decimal(5,2),
     ADD COLUMN IF NOT EXISTS altura_nueva decimal(5,2), ADD COLUMN IF NOT EXISTS presion_sistolica_nueva integer,
     ADD COLUMN IF NOT EXISTS presion_diastolica_nueva integer, ADD COLUMN IF NOT EXISTS temperatura_nueva decimal(3,1);
   ```
2. **Quién modifica una consulta:** solo el médico que la atendió (dueño de la cita). Cualquier médico
   puede *consultarla* en el expediente. Es fácil de relajar: la comprobación está en
   `ConsultaServiceImpl.modificar`.

## Archivos

```
backend/src/main/java/com/josbar/medisistemas/
├── config/SecurityConfig.java                      (reglas por rol del médico)
├── controllers/clinico/ConsultaController.java     (reescrito: @Valid, JWT, GET /mias)
├── controllers/clinico/CitaController.java         (+ /mis-citas, /mis-citas/{id})
├── services/ConsultaService.java, impl/ConsultaServiceImpl.java   (reglas de UC-MED-003/004)
├── services/CitaService.java, impl/CitaServiceImpl.java           (+ agenda del médico)
├── services/impl/ExpedienteClinicoServiceImpl.java (historial más reciente primero)
├── services/impl/AuditoriaServiceImpl.java         (auditoría más reciente primero)
├── mappers/impl/ConsultaMapper.java                (respuesta con fecha, médico y paciente; update de signos)
├── mappers/impl/AuditoriaConsultaMapper.java       (+ nombre del médico, motivo y signos vitales)
├── repositories/ConsultaRepository, CitaRepository, AuditoriaConsultaRepository
├── domain/entities/AuditoriaConsultaEntity.java    (+ 10 columnas)
└── domain/dtos/consulta/*, domain/dtos/auditoria/AuditoriaConsultaResponseDTO.java
database/schema.sql                                  (AuditoriaConsulta + comentario)
```

## Endpoints y reglas

| Método y ruta | Rol | Qué hace |
| --- | --- | --- |
| `GET /citas/mis-citas?desde&hasta` | MEDICO | Sus citas del rango (el id sale del `sub` del JWT). 400 si `desde > hasta`. |
| `GET /citas/mis-citas/{id}` | MEDICO | Una cita propia; 404 si no existe **o no es suya**. |
| `PATCH /citas/{id}/llamado` | MEDICO | (ya existía) solicita a la secretaria llamar al paciente presente. |
| `POST /consultas` | MEDICO | Registra la consulta (UC-MED-003). |
| `GET /consultas/mias?fecha` | MEDICO | Consultas que el médico registró en esa fecha. |
| `GET /consultas/{id}` | MEDICO | Consulta con fecha, médico, paciente y signos vitales. |
| `PUT /consultas/{id}` | MEDICO | Modifica la consulta con auditoría (UC-MED-004). |
| `GET /auditorias/consultas/{id}` | MEDICO | Bitácora, más reciente primero. |
| `GET /expedientes/paciente/{id}` | MEDICO | Paciente + historial (más reciente primero) + documentos. |
| `GET /catalogos/motivos-consulta` | autenticado | Catálogo `MotivoModificacionConsulta`. |

**Registrar consulta** (`ConsultaServiceImpl.registrar`). Validación en el DTO: `idCita`, `motivoConsulta`,
`diagnostico`, `tratamiento` (no vacíos) y `signosVitalesRequestDTO` completo (obligatorios; límites
amplios solo para descartar valores imposibles: peso 0.1–500 kg, altura 20–260 **cm**, sistólica 30–300,
diastólica 20–200, temperatura 25–45 °C). `observaciones` es opcional. Reglas de negocio, en este orden:
la cita existe; **es del médico del token**; está en estado **"En espera"**; no tiene ya una consulta
(columna `id_cita` única). Al guardar pasa la cita a **"Atendido"** y publica `CITA_ATENDIDA` (tras el
*commit*). No exige que la llegada esté registrada ni una fecha concreta: el análisis no lo define.

**Modificar consulta** (`ConsultaServiceImpl.modificar`). El DTO exige `idMotivoModificacionConsulta`
(catálogo, no texto libre, igual que en documentos). Los campos no enviados no cambian; los textos
enviados no pueden ser vacíos; `signosVitalesRequestDTO`, si se envía, va completo. Solo el médico dueño
de la cita. Se captura el estado anterior, se aplica el cambio, se captura el nuevo y **si no hay
diferencias responde 400** ("No se detectaron cambios"). Si las hay se guarda `AuditoriaConsulta` con
**todos** los campos anteriores y nuevos (texto y signos vitales), el médico (`sub` del JWT), la fecha/hora
y el motivo. La auditoría nunca se borra.

**Seguridad** (`SecurityConfig`): el médico ya **no** puede leer `GET /citas/**` general (agenda diaria,
rango, por médico, disponibilidad): solo `/citas/mis-citas/**`, porque "el médico consulta únicamente sus
propias citas". `/consultas/**`, `/expedientes/**` y `/auditorias/consultas/**` son **solo MEDICO**: la
secretaria no ve información clínica. Siguen disponibles para el médico `GET /pacientes/**` (buscador) y
`GET /documentos/**` (ver y descargar documentos del expediente).

## Integración con Secretaria (sinergia)

```
Secretaria: programa la cita ─► CITA_CREADA ─► agenda del médico se actualiza (WebSocket)
Secretaria: marca llegada     ─► CITA_ACTUALIZADA ─► entra a la cola del médico
Médico: PATCH /citas/{id}/llamado ─► CITA_ACTUALIZADA ─► Inicio y Llamador de la secretaria
Médico: POST /consultas       ─► cita "Atendido" + CITA_ATENDIDA ─► secretaria y médico dueño
Secretaria: sube documentos   ─► el médico los ve en el expediente (GET /documentos/**)
```
Los eventos se filtran por rol en `CitaWebSocketHandler`: la secretaria recibe todos; un médico solo los
de sus citas (verificado con un segundo médico que no recibió nada).

## Consideraciones

- **Verificado** contra PostgreSQL y **Supabase reales** con un médico de prueba (ya eliminado): seguridad
  por rol (403 a secretaria en rutas clínicas y de médico en `agenda-diaria`), 400 por validaciones y por
  reglas (llamado sin llegada, consulta repetida, médico ajeno, sin motivo, texto vacío, sin cambios),
  registro, modificación con auditoría de texto y signos vitales, y eventos WebSocket. `mvnw compile` sin errores.
- **Resuelto después:** `GlobalExceptionHandler` no tenía manejador para JSON mal formado ni rutas
  inexistentes (respondían 500); se agregaron en `correcciones6.md`.
- Un token de médico sigue válido hasta vencer aunque se inactive el usuario (limitación ya conocida de JWT).
- `GET /consultas/{id}` lo puede leer cualquier médico (el análisis permite consultar cualquier expediente).
- El médico no ve su propia especialidad en la interfaz: `GET /medicos/**` es solo de administrador y
  secretaria, y el JWT no la incluye.
- Reiniciar el backend y ejecutar el `ALTER TABLE` indicado arriba.
