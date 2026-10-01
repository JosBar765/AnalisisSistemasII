# MediSistema

Sistema de gestión clínica (consulta externa). Backend en Spring Boot (Java), Frontend en Angular y base de datos
PostgreSQL en Docker. Los documentos clínicos se guardan en Supabase Storage.

Documentación detallada de lo implementado: `.agents/backend/documentacion/` y `.agents/frontend/documentacion/`.
Análisis funcional y reglas de arquitectura: `.agents/`.

## Guía de instalación local

### 1. Requisitos

| Herramienta | Versión | Uso |
|---|---|---|
| Git | cualquiera | clonar el repositorio |
| JDK | 21 | compilar y ejecutar el backend (Spring Boot) |
| Node.js | 22 o superior (se desarrolló con Node 24) | frontend (Angular) |
| Docker Desktop | reciente | base de datos PostgreSQL |

No hace falta instalar Maven ni Angular CLI: el backend incluye Maven Wrapper (`mvnw`) y el frontend trae Angular CLI
como dependencia.

### 2. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd <carpeta-del-proyecto>
```

### 3. Configurar las variables de entorno

En la **raíz** del proyecto, copiar `.env.example` como `.env`:

```bash
cp .env.example .env        # Windows (PowerShell): Copy-Item .env.example .env
```

Completar el `.env` (el mismo archivo lo leen Docker, el backend y el frontend). Ejemplo para desarrollo:

```properties
# Base de datos
DB_HOST=localhost
DB_PORT=6060
DB_NAME=medisistema
DB_USERNAME=medisistema_user
DB_PASSWORD=<contraseña-a-elegir>

# API
SERVER_PORT=6061
JWT_SECRET=<texto-aleatorio-de-al-menos-32-caracteres>
CORS_ALLOWED_ORIGINS=http://localhost:4200

# Frontend
FRONTEND_PORT=4200

# Supabase Storage (solo para subir/ver documentos clínicos)
SUPABASE_URL=
SUPABASE_SERVICE_KEY=
SUPABASE_BUCKET=
```

- `DB_PORT`, `SERVER_PORT` y `FRONTEND_PORT` pueden ser cualquier puerto libre.
- `CORS_ALLOWED_ORIGINS` debe incluir `http://` (si se omite, se usa `http://localhost:<FRONTEND_PORT>`).
- `API_URL_PRODUCTION` se deja vacío en local.
- Sin las variables de Supabase todo funciona, **excepto** subir y consultar documentos (responde error 503). Para
  usarlos hay que crear un bucket **privado** en Supabase y completar las tres variables.

### 4. Levantar la base de datos

Con Docker Desktop abierto, desde la raíz:

```bash
docker compose up -d
docker compose ps        # debe mostrar el contenedor "healthy"
```

La primera vez crea la base y carga automáticamente el esquema (`database/schema.sql`) y los datos iniciales
(`database/seed.sql`). Para reiniciarla desde cero: `docker compose down -v` y volver a ejecutar `docker compose up -d`.

### 5. Levantar el backend

En una terminal, **desde la carpeta `backend`** (es necesario porque lee el `.env` de la carpeta superior):

```bash
cd backend
./mvnw spring-boot:run          # Windows (PowerShell): .\mvnw spring-boot:run
```

Está listo cuando aparece `Started MedisistemaApplication`. La API queda en `http://localhost:<SERVER_PORT>`. Lee el
`.env` solo al arrancar: si se cambia, hay que reiniciarlo.

### 6. Levantar el frontend

En otra terminal:

```bash
cd frontend
npm install
npm start
```

`npm start` genera la configuración de URLs a partir del `.env` y abre el servidor de desarrollo en
`http://localhost:<FRONTEND_PORT>`.

### 7. Ingresar al sistema

Abrir `http://localhost:4200` e iniciar sesión con el administrador inicial (datos de `database/seed.sql`):

- **Correo:** `admin@medisistema.com`
- **Contraseña:** `Admin123*`

Desde el módulo de Administración se crean el resto de usuarios. Para tener un médico operativo: registrar un usuario
con rol **Médico**, luego registrarlo en **Médicos** (con su especialidad y colegiado) y configurar sus períodos en
**Jornadas Médicas**. Las secretarias se crean como usuarios con rol **Secretaria**.

### 8. Problemas frecuentes

| Síntoma | Causa y solución |
|---|---|
| El login dice "No se pudo conectar con el servidor" | El backend no está en marcha, o `CORS_ALLOWED_ORIGINS` no coincide con la URL del frontend (con `http://`). Corregir y reiniciar el backend |
| El backend falla con `Could not resolve placeholder '…'` | Falta una variable en el `.env` (por ejemplo `JWT_SECRET`) |
| Error de conexión a la base de datos | Docker no está corriendo, o `DB_HOST`, `DB_PORT`, `DB_USERNAME` y `DB_PASSWORD` no coinciden con los usados al crear el contenedor (si se cambian, repetir `docker compose down -v`) |
| `Port … is already in use` | Cambiar `SERVER_PORT`, `DB_PORT` o `FRONTEND_PORT` en el `.env` |
| Subir o ver documentos da error 503 | Faltan las variables de Supabase o el bucket no existe |

## Estructura del repositorio

```
├── backend/            Spring Boot (API REST + WebSocket)
├── frontend/           Angular (SPA)
├── database/           Esquema (schema.sql) y datos iniciales (seed.sql) de PostgreSQL
├── docker-compose.yml  PostgreSQL local
├── .env.example        Plantilla de variables de entorno (copiar como .env)
├── pruebas/            Formato y sets de pruebas
├── vistas/             Prototipos HTML/CSS (referencia visual, no se ejecutan)
└── .agents/            Análisis, reglas de arquitectura y documentación de cambios
```
