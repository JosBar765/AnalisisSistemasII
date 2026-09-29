# Creación — Módulo Administrador (Backend)

## Objetivo

Implementar de forma funcional la API REST del módulo de Administrador (UC-ADM-001 a
UC-ADM-005), conectada a PostgreSQL vía Docker, respetando la arquitectura Controller →
Service → Repository → Entity/DTO/Mapper ya definida en el scaffold del proyecto.

El scaffold existente ya traía entidades, DTOs, mappers y repositories para todos los
módulos (Admin, Paciente, Clínico), y controllers que referenciaban interfaces de
servicio (`services.*`) que no existían todavía en ningún lugar del código — el
Backend no compilaba. Para que el módulo Admin arrancara de verdad fue necesario que
**toda** la aplicación Spring compilara, así que se completaron también los módulos
fuera de Admin con un CRUD mínimo (ver sección "Fuera de alcance" más abajo).

## Archivos creados

```
backend/.mvn/wrapper/maven-wrapper.properties   (reubicado desde la raíz del repo)

backend/src/main/java/com/josbar/medisistemas/
├── services/                            (interfaces, una por módulo)
│   ├── UsuarioService.java
│   ├── EspecialidadService.java
│   ├── MedicoService.java
│   ├── JornadaMedicaService.java
│   ├── DashboardService.java
│   ├── AuditoriaService.java
│   ├── CatalogoService.java
│   ├── CategoriaDocumentoService.java
│   ├── AuthService.java
│   ├── PacienteService.java
│   ├── CitaService.java
│   ├── ConsultaService.java
│   ├── DocumentoService.java
│   └── ExpedienteClinicoService.java
├── services/impl/                       (una implementación por interfaz, mismo nombre + Impl)
├── config/
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── exceptions/
│   ├── ResourceNotFoundException.java
│   ├── BusinessRuleException.java
│   ├── InvalidCredentialsException.java
│   └── GlobalExceptionHandler.java
├── domain/dtos/error/ErrorResponseDTO.java
└── repositories/PacienteRepository.java  (única entidad sin repository en el scaffold)

docker-compose.yml
docker/init/02_seed.sql
```

## Archivos modificados

- `backend/src/main/resources/application.properties`: credenciales de PostgreSQL vía
  variables de entorno (con defaults para Docker local), `ddl-auto=none`, CORS.
- `backend/src/main/java/com/josbar/medisistemas/controllers/auth/AuthController.java`:
  faltaba el `import` de `AuthService`, no compilaba.
- `backend/src/main/java/com/josbar/medisistemas/mappers/impl/CatalogoMapper.java`:
  el `toResponse` era un stub que devolvía un DTO vacío; ahora mapea de verdad
  `Rol`, `Especialidad`, `DiaSemana`, `EstadoCita`, `CategoriaDocumento` y los dos
  `MotivoModificacion*` por `instanceof`.
- Repositories existentes: se agregaron métodos derivados/`@Query` necesarios para las
  reglas de negocio (ver detalle abajo).
- `domain/dtos/documento/SubirDocumentoRequestDTO.java`: se agregó `idUsuarioCarga`
  (ver nota de JWT pendiente).
- `.agents/MediSistema.sql`: `Usuario.estado` y `Paciente.estado` se cambiaron de
  `bit` a `boolean`. Es una corrección técnica, no una decisión de negocio: las
  entidades JPA usan `Boolean`, y el tipo `bit` de PostgreSQL no es compatible con
  ese mapeo (es un tipo de cadena de bits, no un booleano). Sin este cambio la
  aplicación no habría podido leer/escribir esas dos columnas.

## Funcionalidades (UC-ADM-001 a UC-ADM-005)

- **UC-ADM-001 — Usuarios y Roles**: CRUD completo en `POST/GET/PUT /usuarios`,
  `PATCH /usuarios/{id}/estado`, `PATCH /usuarios/{id}/contrasenia`. La contraseña se
  guarda con BCrypt (`PasswordEncoder`, ver `SecurityConfig`).
- **UC-ADM-002 — Especialidades**: `POST/PUT/DELETE /especialidades` (creación/edición/
  eliminación) + `GET /catalogos/especialidades` (listado, vía `CatalogoController`
  existente). Regla de negocio: `EspecialidadServiceImpl.eliminar()` verifica
  `MedicoRepository.existsByEspecialidadId()` y lanza `BusinessRuleException` → HTTP 400
  si hay médicos asociados.
- **UC-ADM-003 — Médicos**: `POST/GET/PUT /medicos`. `MedicoServiceImpl.save()` valida
  que el usuario asociado exista y esté activo. Un médico pertenece a una única
  especialidad (columna `id_especialidad` no nula). Activar/inactivar un médico se
  resuelve reutilizando `PATCH /usuarios/{id}/estado` — el `id` de `Medico` es el mismo
  que el de `Usuario` (`@MapsId` en `MedicoEntity`), así que no se duplicó un endpoint
  de estado en `/medicos`.
- **UC-ADM-004 — Jornada Médica**: `POST /jornadas`, `GET /jornadas/medico/{id}`,
  `PUT/DELETE /jornadas/{id}`. Permite turnos fragmentados (varias filas por médico +
  día). Valida hora de inicio anterior a hora de fin y duración de consulta > 0.
- **UC-ADM-005 — Dashboard**: `GET /dashboard` retorna pacientes registrados, consultas
  realizadas, citas canceladas (`CitaRepository.countByEstadoCitaEntityEstadoCita`) y
  consultas por médico (consulta JPQL con constructor expression en
  `ConsultaRepository.obtenerConsultasPorMedico()`).

## Infraestructura (Docker + PostgreSQL)

- `docker-compose.yml` levanta PostgreSQL 15 en el puerto 5432, base `medisistema_db`,
  usuario `postgres` / contraseña `postgres_password`.
- El esquema se monta directo desde `.agents/MediSistema.sql` (fuente de verdad, sin
  duplicarlo) como `01_schema.sql`; `docker/init/02_seed.sql` agrega los datos
  iniciales: roles, días de la semana, estados de cita, especialidades de ejemplo,
  categorías de documento, motivos de modificación y un usuario administrador
  (`admin@medisistema.com` / `Admin123*`, hash BCrypt).
- `application.properties` usa `ddl-auto=none`: el esquema es responsabilidad exclusiva
  del script SQL, Hibernate no debe intentar crearlo ni validarlo.

## Integración

```
UsuarioController / MedicoController / EspecialidadController / JornadaMedicaController / DashboardController
        │
        ▼
services.*Service (interfaz)
        │
        ▼
services.impl.*ServiceImpl
        │
        ▼
repositories.*Repository (Spring Data JPA)
        │
        ▼
PostgreSQL (Docker, ver docker-compose.yml)
```

Los errores de negocio (`BusinessRuleException`), de recurso no encontrado
(`ResourceNotFoundException`), de credenciales inválidas (`InvalidCredentialsException`)
y de integridad de datos (`DataIntegrityViolationException`, p. ej. una FK inexistente)
se centralizan en `GlobalExceptionHandler` y se traducen a respuestas JSON consistentes
(`ErrorResponseDTO`) con el código HTTP apropiado (400/404/401/500).

## Fuera de alcance de este módulo (pero necesario para compilar)

Los controllers de Paciente, Cita, Consulta, Documento y Expediente Clínico ya
existían en el scaffold (para las fases de Secretaría/Médico). Para que la aplicación
Spring arrancara se les dio una implementación de servicio CRUD mínima y correcta,
sin profundizar en las reglas de negocio de esos módulos (cola de atención diaria,
auditoría de modificaciones de consulta/documento, etc. — ver UC-SEC y UC-MED en
`.agents/analisis/analisis_medisistema.md`). Quedan documentados aquí para que la
siguiente fase los retome, no para darlos por completos.

## Consideraciones / limitaciones conocidas

1. **No hay JWT todavía.** `SecurityConfig` deja todos los endpoints abiertos
   (`permitAll`) y `AuthServiceImpl` valida credenciales reales contra la base de datos
   pero devuelve un token opaco sin firma (`UUID`), no un JWT. El Frontend, además,
   ignora esa respuesta y hace bypass del login (ver documentación de Frontend). Debe
   implementarse un filtro JWT + autorización por rol antes de pasar a producción, tal
   como exige `.agents/reglas_despliegue.md`.
2. Como consecuencia de no tener JWT, `SubirDocumentoRequestDTO` recibe `idUsuarioCarga`
   explícitamente en el request en vez de tomarlo del contexto de seguridad — es un
   parche temporal, documentado en el propio DTO y en `DocumentoMapper`.
3. `DocumentoServiceImpl` y `ConsultaServiceImpl` no generan registros de auditoría al
   modificar (`AuditoriaDocumento`/`AuditoriaConsulta`); esa regla pertenece al alcance
   de Secretaría/Médico, no al de este módulo.
4. Este entorno de desarrollo no tiene Docker instalado, por lo que **no fue posible
   levantar el contenedor de PostgreSQL ni probar la integración end-to-end contra una
   base de datos real**. Sí se verificó que el proyecto compila limpio
   (`mvnw compile`, `mvnw test-compile`) y que el contexto de Spring arranca y resuelve
   correctamente todos los beans hasta el punto de intentar la conexión JDBC (ver
   `.agents/backend/documentacion/` — instrucciones de ejecución local más abajo).

## Cómo ejecutar el backend localmente

Requisitos: JDK 21 y Docker (Docker Desktop en Windows/Mac, o Docker Engine en Linux).
No se necesita instalar Maven aparte: el proyecto trae Maven Wrapper (`mvnw` / `mvnw.cmd`).

```bash
# 1. Levantar PostgreSQL (desde la raíz del repo)
docker compose up -d

# 2. Compilar y ejecutar el backend (desde backend/)
cd backend
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

La API queda disponible en `http://localhost:8080`. El usuario administrador inicial
es `admin@medisistema.com` / `Admin123*`.
