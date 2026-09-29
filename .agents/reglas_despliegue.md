# REGLAS DE DESPLIEGUE — MediSistema

## 1. ARQUITECTURA DE DESPLIEGUE

MediSistema será desplegado utilizando **Railway** como plataforma de ejecución de la aplicación y **Supabase** como proveedor de servicios de datos.

La arquitectura general será:

```text
                         INTERNET
                             │
              ┌──────────────┴──────────────┐
              │                             │
              ▼                             │
       ┌──────────────┐                     │
       │   Railway    │                     │
       │              │                     │
       │   Frontend   │                     │
       │    Angular   │                     │
       │       │      │                     │
       │       │ HTTP │                     │
       │       │ WSS  │                     │
       │       ▼      │                     │
       │   Backend    │                     │
       │ Spring Boot  │                     │
       └───────┬──────┘                     │
               │                            │
               │ HTTPS / JDBC               │
               │                            │
               ▼                            │
       ┌──────────────┐                     │
       │   Supabase   │◄────────────────────┘
       │              │
       │ PostgreSQL   │
       │   Storage    │
       └──────────────┘
````

La aplicación se encuentra organizada como un repositorio que contiene dos aplicaciones independientes:

```text
MediSistema/
├── backend/
└── frontend/
```

Estas aplicaciones serán desplegadas como servicios independientes dentro de Railway.

El Backend proporcionará dos mecanismos principales de comunicación:

```text
HTTP/HTTPS
    │
    └── API REST

WebSocket / WSS
    │
    └── Comunicación en tiempo real
```

La comunicación WebSocket será utilizada únicamente cuando una funcionalidad requiera actualización de información en tiempo real.

---

# 2. SERVICIOS DE RAILWAY

Railway será responsable de ejecutar las aplicaciones de MediSistema.

La configuración esperada será:

```text
Railway Project

│
├── Backend Service
│   └── Spring Boot API + WebSocket
│
└── Frontend Service
    └── Angular
```

Cada servicio debe utilizar la carpeta correspondiente del repositorio.

## 2.1 Backend

El backend se encuentra en:

```text
/backend
```

Esta carpeta contiene el proyecto Spring Boot:

```text
backend/
├── src/
├── mvnw
├── mvnw.cmd
└── pom.xml
```

Railway debe utilizar esta carpeta como directorio raíz del servicio Backend.

El Backend será responsable de:

* Exponer la API REST.
* Proporcionar comunicación WebSocket.
* Autenticar usuarios.
* Generar y validar tokens JWT.
* Aplicar autorización basada en roles.
* Ejecutar las reglas de negocio.
* Acceder a PostgreSQL.
* Gestionar documentos mediante Supabase Storage.
* Validar los datos recibidos desde Angular.
* Registrar auditorías.
* Controlar el acceso a información clínica.
* Emitir eventos de actualización en tiempo real cuando corresponda.

El Backend será el único componente autorizado para acceder directamente a la base de datos PostgreSQL.

---

## 2.2 Frontend

El frontend se encuentra en:

```text
/frontend
```

Esta carpeta contiene la aplicación Angular.

Railway debe utilizar esta carpeta como directorio raíz del servicio Frontend.

El Frontend será responsable de:

* Mostrar la interfaz de usuario.
* Gestionar las rutas de Angular.
* Gestionar formularios.
* Mostrar información recibida desde la API.
* Enviar solicitudes HTTP al Backend.
* Mantener una conexión WebSocket cuando una funcionalidad lo requiera.
* Gestionar el token JWT en el cliente.
* Aplicar guards para controlar el acceso a las rutas.
* Mostrar u ocultar funcionalidades según el rol del usuario.
* Actualizar la interfaz cuando reciba eventos mediante WebSocket.

El Frontend **NO** debe conectarse directamente a PostgreSQL.

El Frontend **NO** debe implementar reglas de negocio críticas.

---

# 3. COMUNICACIÓN HTTP Y WEBSOCKET

MediSistema utilizará dos mecanismos de comunicación entre Angular y Spring Boot.

## 3.1 HTTP/HTTPS

HTTP/HTTPS será el mecanismo principal para las operaciones normales de la aplicación.

Ejemplo:

```text
Angular
   │
   │ HTTPS
   │ JWT
   ▼
Spring Boot
   │
   ▼
PostgreSQL
```

Se utilizará HTTP para operaciones como:

```text
GET
POST
PUT
PATCH
DELETE
```

Por ejemplo:

```text
GET  /api/pacientes
POST /api/pacientes
GET  /api/citas
POST /api/citas
PUT  /api/consultas/{id}
```

Las operaciones que modifican información deberán ejecutarse mediante la API REST.

---

## 3.2 WebSocket

WebSocket será utilizado para funcionalidades que requieran comunicación en tiempo real entre el Backend y los clientes conectados.

La conexión deberá utilizar:

```text
WSS
```

en producción.

El flujo será:

```text
Angular
   │
   │ WebSocket seguro (WSS)
   ▼
Spring Boot
   │
   │ eventos
   ▼
Clientes conectados
```

WebSocket **NO reemplaza la API REST**.

Ambos mecanismos tendrán responsabilidades diferentes:

```text
REST API
→ Solicitudes y operaciones sobre recursos.

WebSocket
→ Notificaciones y actualizaciones en tiempo real.
```

---

# 4. USO DE WEBSOCKET EN MEDISISTEMA

Un caso principal de uso será la gestión de citas.

Por ejemplo:

```text
Secretaria
    │
    ▼
Angular
    │
    │ POST /api/citas
    ▼
Spring Boot
    │
    ├── valida JWT
    ├── valida rol
    ├── valida reglas de negocio
    ├── registra Cita
    │
    ▼
PostgreSQL
    │
    ▼
Spring Boot
    │
    │ evento WebSocket
    ▼
Doctor conectado
    │
    ▼
Angular actualiza la interfaz
```

De esta manera, si una secretaria registra una nueva cita para el día actual, el médico correspondiente podrá recibir una notificación sin tener que actualizar manualmente la página.

---

## 4.1 Ejemplo de evento

Un evento podría representar conceptualmente:

```json
{
  "tipo": "CITA_CREADA",
  "citaId": 25,
  "medicoId": 8,
  "fecha": "2026-08-22",
  "hora": "10:30"
}
```

El Frontend del médico podrá recibir este evento y actualizar únicamente la información necesaria.

Por ejemplo:

```text
Nueva cita registrada

        ↓

WebSocket

        ↓

Feature de citas

        ↓

Actualizar agenda
```

El evento WebSocket **no debe contener información clínica innecesaria o sensible**.

---

# 5. RESPONSABILIDAD DE WEBSOCKET

WebSocket debe considerarse una preocupación de comunicación transversal.

La implementación debe mantener una separación clara entre:

```text
Regla de negocio
       │
       ▼
Servicio de aplicación
       │
       ▼
Evento
       │
       ▼
WebSocket
```

La lógica de negocio **NO debe depender directamente de detalles de WebSocket**.

Por ejemplo, un servicio de citas no debería contener lógica excesivamente acoplada al mecanismo de transporte.

Preferir conceptualmente:

```text
CitaService
    │
    ├── crea la cita
    │
    └── genera evento
              │
              ▼
       WebSocket Service
              │
              ▼
        Clientes Angular
```

El objetivo es mantener **alta cohesión y bajo acoplamiento**.

---

# 6. SUPABASE

Supabase será utilizado como proveedor de servicios de datos.

Se utilizarán principalmente dos servicios:

```text
Supabase

├── PostgreSQL
└── Storage
```

## 6.1 PostgreSQL

La base de datos PostgreSQL de Supabase almacenará toda la información estructurada de MediSistema.

Entre las entidades principales se encuentran:

```text
Usuario
Rol
Medico
Especialidad
JornadaMedica
DiaSemana
Paciente
Cita
EstadoCita
Consulta
SignosVitales
Documento
CategoriaDocumento
AuditoriaDocumento
MotivoModificacionDocumento
AuditoriaConsulta
MotivoModificacionConsulta
```

La estructura de la base de datos debe respetar el modelo relacional definido para MediSistema.

Las relaciones mediante claves foráneas deben mantenerse.

---

# 7. SUPABASE STORAGE

Supabase Storage será utilizado para almacenar los archivos clínicos asociados a los pacientes.

Los archivos NO deben almacenarse físicamente dentro del proyecto Spring Boot ni dentro del proyecto Angular.

Ejemplo:

```text
Supabase Storage

└── documentos-clinicos/
    ├── paciente-1/
    │   ├── examen.pdf
    │   └── radiografia.jpg
    │
    └── paciente-2/
        └── examen-sangre.pdf
```

La tabla `Documento` almacenará la información necesaria para identificar el archivo.

El campo:

```text
url
```

representará la referencia al archivo almacenado en Supabase Storage.

---

# 8. FLUJO DE COMUNICACIÓN

La arquitectura debe respetar los siguientes flujos.

## Operaciones normales

```text
Angular
   │
   │ HTTP/HTTPS
   │ JWT
   ▼
Spring Boot API
   │
   ├──────────────► Supabase PostgreSQL
   │
   └──────────────► Supabase Storage
```

## Comunicación en tiempo real

```text
Angular
   │
   │ WSS
   ▼
Spring Boot
   │
   │ eventos
   ▼
Angular
```

## Flujo combinado

Una operación puede utilizar ambos mecanismos:

```text
Secretaria
    │
    ▼
Angular
    │
    │ HTTP POST
    ▼
Spring Boot
    │
    ├── procesa operación
    │
    ├── PostgreSQL
    │
    └── genera evento
             │
             │ WebSocket
             ▼
       Angular del médico
             │
             ▼
       Actualiza interfaz
```

No se debe implementar el siguiente flujo:

```text
Angular ─────────► PostgreSQL
```

Ni:

```text
Angular ─────────► Supabase Storage
```

para operaciones que requieran validaciones, autorización o reglas de negocio del sistema.

El Backend debe funcionar como punto central de acceso a los datos y recursos protegidos.

---

# 9. AUTENTICACIÓN Y JWT

MediSistema utilizará autenticación mediante JWT.

El flujo esperado será:

```text
1. Usuario
      │
      ▼
2. Angular
      │
      │ POST /api/auth/login
      ▼
3. Spring Boot
      │
      │ valida credenciales
      ▼
4. JWT
      │
      ▼
5. Angular
```

Posteriormente, Angular deberá enviar el JWT en las solicitudes protegidas:

```text
Authorization: Bearer <JWT>
```

El Backend deberá validar el token antes de permitir el acceso a recursos protegidos.

---

# 10. AUTENTICACIÓN DE WEBSOCKET

La conexión WebSocket también debe respetar el modelo de seguridad de MediSistema.

No se debe asumir que un usuario autenticado en Angular puede conectarse libremente a cualquier canal WebSocket.

El Backend debe validar la identidad y los permisos del usuario antes de permitir el acceso a los canales correspondientes.

Conceptualmente:

```text
Angular
   │
   │ conexión WSS
   │ credenciales de autenticación
   ▼
Spring Security
   │
   ├── valida identidad
   ├── valida JWT
   └── valida permisos
           │
           ▼
      WebSocket
           │
           ▼
   Canales autorizados
```

Los usuarios únicamente deben recibir eventos que correspondan a sus permisos y contexto funcional.

Por ejemplo:

```text
ADMINISTRADOR
    │
    └── eventos administrativos autorizados

SECRETARIA
    │
    └── eventos relacionados con sus funcionalidades

MEDICO
    │
    └── eventos relacionados con sus citas y consultas autorizadas
```

Nunca se debe utilizar WebSocket como mecanismo para evadir las reglas de autorización del Backend.

---

# 11. AUTORIZACIÓN POR ROLES

MediSistema maneja diferentes roles de usuario.

Los roles principales son:

```text
ADMINISTRADOR

SECRETARIA

MEDICO
```

El Backend debe ser la autoridad final para determinar si un usuario tiene permisos para ejecutar una operación.

El Frontend puede utilizar guards para mejorar la experiencia del usuario, pero los guards del Frontend NO sustituyen la autorización del Backend.

La misma regla aplica a WebSocket.

---

# 12. VARIABLES DE ENTORNO

No se deben almacenar credenciales, contraseñas, tokens, claves privadas ni URLs sensibles directamente en el código fuente.

Las configuraciones dependientes del entorno deben utilizar variables de entorno.

## Backend

El Backend deberá recibir mediante variables de entorno información como:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

JWT_SECRET

SUPABASE_URL
SUPABASE_KEY
```

Los nombres exactos pueden variar según la implementación, pero las credenciales no deben escribirse directamente en clases Java ni en archivos que sean enviados al repositorio.

---

## Frontend

El Frontend deberá utilizar configuración específica para conocer la URL pública de la API y del WebSocket.

Ejemplo conceptual:

```text
API_URL=https://<backend-url>

WS_URL=wss://<backend-url>
```

En desarrollo:

```text
API_URL=http://localhost:8080

WS_URL=ws://localhost:8080
```

En producción:

```text
API_URL=https://<backend-railway-url>

WS_URL=wss://<backend-railway-url>
```

No debe ser necesario modificar manualmente múltiples componentes para cambiar estas URLs.

---

# 13. CORS Y WEBSOCKET

Como Angular y Spring Boot serán desplegados como servicios independientes, tendrán diferentes orígenes.

Por ejemplo:

```text
Frontend

https://<frontend-railway-url>
```

Backend:

```text
https://<backend-railway-url>
```

El Backend deberá configurar CORS para permitir solicitudes únicamente desde los orígenes autorizados.

No utilizar:

```text
Access-Control-Allow-Origin: *
```

para endpoints protegidos en producción.

La configuración WebSocket también debe restringir los orígenes permitidos.

No se debe permitir que cualquier sitio web establezca conexiones WebSocket con el Backend.

---

# 14. BASE DE DATOS

La aplicación deberá utilizar la instancia PostgreSQL proporcionada por Supabase.

Spring Boot será el encargado de establecer la conexión.

El flujo será:

```text
Spring Boot

     │

     │ JDBC / PostgreSQL

     ▼

Supabase PostgreSQL
```

La aplicación no debe crear una segunda base de datos dentro de Railway.

Railway ejecutará la aplicación, mientras que Supabase proporcionará PostgreSQL.

```text
Railway

└── Ejecuta aplicaciones

Supabase

├── PostgreSQL
└── Storage
```

---

# 15. DOCUMENTOS CLÍNICOS

Los documentos clínicos deben seguir el siguiente flujo:

```text
Secretaria
    │
    ▼
Angular
    │
    │ Multipart/Form-Data
    ▼
Spring Boot
    │
    ├── valida permisos
    ├── valida archivo
    ├── registra información
    │
    ▼
Supabase Storage
```

Posteriormente, la información del documento se registra en PostgreSQL.

El archivo físico permanece en Supabase Storage.

---

# 16. AUDITORÍA

La aplicación cuenta con:

```text
AuditoriaDocumento
MotivoModificacionDocumento

AuditoriaConsulta
MotivoModificacionConsulta
```

Cuando un documento sea actualizado, no se debe eliminar silenciosamente su información anterior.

Debe mantenerse el registro de auditoría correspondiente.

La misma lógica aplica para las modificaciones de consultas médicas.

---

# 17. SEGURIDAD DE CREDENCIALES

Está estrictamente prohibido subir al repositorio:

```text
.env
application-production.properties
Credenciales de Supabase
Contraseñas de PostgreSQL
JWT_SECRET
Tokens
API Keys privadas
```

Las credenciales de producción deben configurarse mediante las variables de entorno proporcionadas por Railway.

El repositorio debe contener únicamente configuraciones no sensibles o ejemplos.

---

# 18. ARQUITECTURA FINAL

La arquitectura de producción esperada para MediSistema es:

```text
                           USUARIO
                              │
                              ▼
                     ┌────────────────┐
                     │    Railway     │
                     │                │
                     │    Angular     │
                     │   Frontend     │
                     └───────┬────────┘
                             │
                    ┌────────┴────────┐
                    │                 │
                    │ HTTPS           │ WSS
                    │ JWT             │
                    ▼                 ▼
             ┌────────────────────────────┐
             │          Railway           │
             │                            │
             │       Spring Boot          │
             │           API              │
             │                            │
             │       REST + WebSocket     │
             └─────────────┬──────────────┘
                           │
                 ┌─────────┴─────────┐
                 │                   │
                 ▼                   ▼
          ┌──────────────┐    ┌──────────────┐
          │   Supabase   │    │   Supabase   │
          │  PostgreSQL  │    │   Storage    │
          │              │    │              │
          │ Información  │    │ Documentos   │
          │ clínica      │    │ clínicos     │
          └──────────────┘    └──────────────┘
```

## Responsabilidades

| Componente          | Responsabilidad                                                      |
| ------------------- | -------------------------------------------------------------------- |
| Angular             | Interfaz de usuario y experiencia del usuario                        |
| Railway             | Ejecución y despliegue de las aplicaciones                           |
| Spring Boot         | API REST, WebSocket, lógica de negocio, autenticación y autorización |
| Spring Security     | Seguridad y autorización mediante JWT                                |
| WebSocket           | Comunicación y actualización de información en tiempo real           |
| Supabase PostgreSQL | Persistencia de información estructurada                             |
| Supabase Storage    | Almacenamiento de documentos clínicos                                |

---

# 19. REGLA PRINCIPAL DE DESPLIEGUE

La arquitectura debe mantener una separación clara de responsabilidades:

```text
Angular

→ Presentación + interacción + recepción de eventos

Spring Boot

→ API + WebSocket + Seguridad + Lógica de negocio

Supabase PostgreSQL

→ Datos estructurados

Supabase Storage

→ Archivos clínicos

Railway

→ Ejecución y despliegue
```

No se debe trasladar lógica de negocio al Frontend únicamente para evitar implementarla en el Backend.

El Backend debe ser considerado la **fuente de verdad** para:

* Autenticación.
* Autorización.
* Roles.
* Validaciones críticas.
* Reglas de negocio.
* Acceso a PostgreSQL.
* Acceso a documentos protegidos.
* Auditoría.
* Generación y distribución de eventos WebSocket.

WebSocket debe utilizarse exclusivamente para comunicación en tiempo real y **no debe convertirse en un reemplazo de la API REST**.

Las operaciones de creación, modificación y eliminación de información deben continuar ejecutándose mediante la API REST. Posteriormente, el Backend podrá emitir eventos WebSocket para notificar a los clientes que necesiten actualizar su información.

La arquitectura debe conservar los principios de **alta cohesión y bajo acoplamiento**, manteniendo cada responsabilidad en el componente que corresponde.