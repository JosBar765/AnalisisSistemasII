# MediSistema

Sistema de gestión clínica. Backend en Spring Boot (Java), Frontend en Angular,
base de datos PostgreSQL en Docker.

Documentación detallada de lo implementado: `.agents/backend/documentacion/` y
`.agents/frontend/documentacion/`. Reglas de negocio y arquitectura: `.agents/`.

## Requisitos (para correr el proyecto en cualquier computadora)

| Herramienta | Versión | Para qué | Dónde descargar |
|---|---|---|---|
| **JDK** | 21 | Compilar y correr el Backend | https://learn.microsoft.com/java/openjdk/download (o cualquier distribución OpenJDK 21, ej. Temurin: https://adoptium.net) |
| **Docker Desktop** | reciente | Levantar PostgreSQL | https://www.docker.com/products/docker-desktop |
| **Node.js** | 20+ | Compilar y correr el Frontend | https://nodejs.org |

No es necesario instalar Maven aparte: el Backend incluye Maven Wrapper (`mvnw` /
`mvnw.cmd`), que descarga Maven automáticamente la primera vez que se ejecuta.
No es necesario instalar Angular CLI globalmente: `npm install` en `frontend/`
ya lo trae como dependencia de desarrollo (se usa vía `npm start`).

## Pasos para levantar el proyecto

```bash
# 1. Clonar el repositorio
git clone <url-del-repo>
cd ProyectoFinal_AnalisisSistemasII   # o el nombre de la carpeta

# 2. Levantar PostgreSQL con Docker (carga el esquema + datos iniciales automáticamente)
docker compose up -d

# 3. Levantar el Backend (puerto 8080)
cd backend
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows (PowerShell o CMD)

# 4. En otra terminal, levantar el Frontend (puerto 4200)
cd frontend
npm install
npm start
```

Luego abrir `http://localhost:4200` en el navegador.

## Usuario administrador inicial (seed data)

- Correo: `admin@medisistema.com`
- Contraseña: `Admin123*`

El login del Frontend actualmente hace **bypass** (no valida credenciales, redirige
directo al dashboard) — ver `.agents/frontend/documentacion/creacion_modulo_admin.md`
para el detalle y las limitaciones conocidas (no hay JWT todavía).

## Estructura del repositorio

```
├── backend/          Spring Boot (API REST)
├── frontend/          Angular (SPA)
├── docker-compose.yml PostgreSQL local
├── docker/init/        Seed data (el esquema se toma de .agents/MediSistema.sql)
├── vistas/             Prototipos HTML/CSS (referencia visual, no se ejecutan)
└── .agents/            Análisis, reglas de arquitectura y documentación de cambios
```
