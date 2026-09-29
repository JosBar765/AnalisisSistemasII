# REGLAS DE ESTRUCTURA — MediSistema

## ESTRUCTURA DEL PROYECTO

El proyecto **MediSistema** está compuesto por dos aplicaciones principales:

* **Backend:** API REST desarrollada con Spring Boot.
* **Frontend:** Aplicación web desarrollada con Angular.

La estructura general del proyecto es:

```text
├── backend
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── josbar
│   │   │   └── resources
│   │   │       └── application.properties
│   │   └── test
│   │       └── java
│   │           └── com
│   │               └── josbar
│   │                   └── medisistemas
│   │                       └── config
│   │                       └── controllers
│   │                       └── domain
│   │                       └── exceptions
│   │                       └── mappers
│   │                       └── repositories
│   │                       └── security
│   │                       └── services
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
│
└── frontend
    └── src
        ├── app
        │   ├── core
        │   │   ├── guards
        │   │   ├── interceptors
        │   │   ├── models
        │   │   └── services
        │   │
        │   ├── features
        │   │
        │   ├── layouts
        │   │   ├── login
        │   │   └── main
        │   │
        │   ├── shared
        │   │   ├── components
        │   │   ├── directives
        │   │   ├── models
        │   │   └── pipes
        │   │
        │   ├── app.component.ts
        │   └── app.route.ts
        │
        ├── assets
        ├── environments
        ├── index.html
        └── styles.css
```

---

### `core/`

Contiene elementos que son utilizados por diferentes partes de la aplicación y que representan funcionalidades globales.

```text
core/
├── guards/
├── interceptors/
├── models/
└── services/
```

#### `guards/`

Contiene los guards encargados de controlar el acceso a las rutas de Angular.

Ejemplos:

```text
auth.guard.ts
role.guard.ts
```

* `auth.guard`: verifica si el usuario está autenticado.
* `role.guard`: verifica si el usuario posee el rol necesario para acceder a una funcionalidad.

#### `interceptors/`

Contiene interceptores HTTP globales.

Por ejemplo:

```text
auth.interceptor.ts
```

Este interceptor será responsable de agregar el token JWT a las solicitudes realizadas hacia la API.

#### `models/`

Contiene modelos utilizados de manera global por la aplicación.

Ejemplos:

```text
auth-response.model.ts
usuario-auth.model.ts
```

#### `services/`

Contiene servicios globales de la aplicación.

Ejemplos:

```text
auth.service.ts
notification.service.ts
websocket.service.ts
```

El `AuthService` será responsable de manejar aspectos relacionados con autenticación, como el inicio y cierre de sesión y el acceso al token JWT.

El `WebSocketService` será responsable de administrar la conexión WebSocket global de la aplicación.

La conexión WebSocket se considera una **preocupación transversal**, por lo que no debe ser implementada directamente dentro de un feature.

El servicio será responsable de:

* Establecer la conexión con el Backend.
* Mantener la conexión mientras sea necesaria.
* Gestionar la desconexión.
* Recibir eventos enviados por Spring Boot.
* Permitir que los features consuman los eventos que necesiten.

La estructura será:

```text
core/
└── services/
    ├── auth.service.ts
    ├── notification.service.ts
    └── websocket.service.ts
```

Los features **no deben crear conexiones WebSocket independientes**.

La comunicación debe seguir el siguiente esquema:

```text
Feature
   │
   ▼
WebSocketService
   │
   ▼
Spring Boot WebSocket
```

Por ejemplo, el feature de citas puede recibir un evento cuando se registre una nueva cita:

```text
Secretaria
    │
    │ REST
    ▼
Spring Boot
    │
    ├── Guarda la cita
    │
    └── Emite evento WebSocket
             │
             ▼
       WebSocketService
             │
             ▼
      Feature de citas
             │
             ▼
       Actualiza la vista
```

WebSocket debe utilizarse únicamente para funcionalidades que requieran **actualización de información en tiempo real**.

Por ejemplo:

* Nueva cita registrada.
* Cita modificada.
* Cita cancelada.
* Cambio de estado de una cita.
* Notificaciones que deban mostrarse inmediatamente.

Las operaciones CRUD normales deben continuar utilizando HTTP/REST.

Los features tampoco deben encargarse de administrar directamente la autenticación de WebSocket. La conexión debe respetar el mecanismo de seguridad basado en JWT y las autorizaciones definidas por el sistema.

---

### `features/`

Esta carpeta contiene las **funcionalidades principales de MediSistema**.

Cada feature representa un módulo funcional del sistema y debe ser lo más independiente posible de los demás.

Ejemplo:

```text
features/
├── usuarios/
├── medicos/
├── pacientes/
├── citas/
├── consultas/
├── expediente/
└── documentos/
```

Los features deben organizarse buscando:

* **Alta cohesión:** todo lo relacionado con una funcionalidad debe permanecer dentro de su propio feature.
* **Bajo acoplamiento:** un feature debe depender lo menos posible de otros features.
* Separación clara de responsabilidades.
* Facilidad para mantener, modificar y probar cada funcionalidad.

Los features pueden consumir eventos WebSocket cuando la funcionalidad lo requiera, pero deben hacerlo mediante `core/services/websocket.service.ts`.

Por ejemplo:

```text
features/citas/
        │
        ▼
websocket.service.ts
        │
        ▼
Evento: cita.creada
        │
        ▼
Actualización de la agenda
```

Un feature no debe conocer ni modificar los detalles internos de la implementación de WebSocket.

---

### `layouts/`

Contiene las estructuras visuales generales de la aplicación.

```text
layouts/
├── login/
└── main/
```

#### `login/`

Layout utilizado para las pantallas relacionadas con autenticación.

#### `main/`

Layout principal de MediSistema, que puede contener elementos globales como:

* Sidebar.
* Navbar.
* Menú de navegación.
* Área principal de contenido.

Las funcionalidades de negocio no deben implementarse directamente dentro de `layouts`.

---

### `shared/`

Contiene elementos **genéricos y reutilizables** que pueden ser utilizados por múltiples features.

```text
shared/
├── components/
├── directives/
├── models/
└── pipes/
```

Ejemplos:

```text
shared/components/
├── loading-spinner/
├── confirmation-dialog/
└── modal/
```

Un componente solamente debe colocarse en `shared` si realmente es reutilizable y no pertenece exclusivamente a una funcionalidad.

Por ejemplo:

```text
PacienteTable
```

**NO** debería estar en `shared`, porque pertenece al feature de pacientes.

En cambio:

```text
LoadingSpinner
```

sí puede estar en `shared`, porque puede ser utilizado por cualquier feature.

---

### Archivos principales

#### `app.component.ts`

Es el componente raíz de la aplicación Angular.

#### `app.route.ts`

Contiene la configuración principal de las rutas de la aplicación y puede delegar las rutas específicas a cada feature.

#### `assets/`

Contiene recursos estáticos como:

* Imágenes.
* Iconos.
* Fuentes.
* Otros archivos estáticos.

#### `environments/`

Contiene configuraciones específicas de cada entorno, por ejemplo, la URL de la API y la configuración necesaria para establecer la conexión con los servicios del Backend.

#### `styles.css`

Contiene estilos globales de la aplicación.

---

# REGLAS PARA LA GENERACIÓN DE FEATURES

Al generar un nuevo feature para MediSistema, se debe seguir una arquitectura **orientada a funcionalidades**, buscando principalmente **alta cohesión y bajo acoplamiento**.

## 1. Cada feature debe representar una funcionalidad

Los features deben corresponder a funcionalidades reales del sistema.

No crear features basados únicamente en tipos técnicos como:

```text
features/
├── tables/
├── forms/
├── services/
└── components/
```

La organización debe hacerse por **funcionalidad**, no por tipo de archivo.

---

## 2. Estructura interna de un feature

Cada feature debe seguir, cuando sea necesario, una estructura similar a:

```text
features/
└── feature1/
    ├── components/
    ├── pages/
    ├── services/
    ├── models/
    └── features1.routes.ts
```

### `pages/`

Contiene las páginas completas asociadas al feature.

Ejemplo:

```text
feature1/
└── pages/
    ├── listar/
    ├── registrar/
    └── editar/
```

Una página representa una vista completa accesible mediante una ruta.

---

### `components/`

Contiene componentes específicos del feature que son utilizados por sus páginas.

Ejemplo:

```text
feature1/
└── components/
    ├── feature1-form/
    ├── feature1-table/
    └── feature1-search/
```

Estos componentes no deben colocarse en `shared` si solamente tienen sentido dentro del contexto de pacientes.

---

### `services/`

Contiene los servicios relacionados exclusivamente con el feature.

Ejemplo:

```text
feature1/
└── services/
    └── feature1.service.ts
```

El servicio será responsable de comunicarse con los endpoints correspondientes de la API de Spring Boot.

No crear un servicio global para funcionalidades específicas.

Incorrecto:

```text
app/services/
└── feature1.service.ts
```

Preferido:

```text
features/feature1/services/
└── feature1.service.ts
```

Los servicios de los features deben utilizar REST para las operaciones normales y pueden consumir `WebSocketService` cuando necesiten recibir actualizaciones en tiempo real.

---

### `models/`

Contiene los modelos e interfaces específicos del feature.

Ejemplo:

```text
feature1/
└── models/
    ├── feature1.model.ts
    └── feature1-request.model.ts
```

Los modelos que solamente tienen sentido para pacientes deben permanecer dentro de `pacientes`.

Los modelos globales, como los relacionados con autenticación, deben permanecer en `core/models`.

---

### `<feature>.routes.ts`

Cada feature debe definir sus propias rutas cuando tenga múltiples vistas.

Ejemplo:

```text
feature1/
└── feature1.routes.ts
```

El archivo principal:

```text
app.routes.ts
```

debe encargarse de integrar las rutas de los features, evitando concentrar toda la configuración de rutas dentro de un único archivo.

---

## 3. Evitar dependencias entre features

Los features deben ser independientes entre sí siempre que sea posible.

Por ejemplo:

```text
features/feature1/
```

no debería importar directamente componentes internos de:

```text
features/feature2/
```

ni:

```text
features/feature3/
```

Si dos funcionalidades necesitan un componente común, este debe evaluarse para determinar si realmente debe trasladarse a:

```text
shared/components/
```

Si necesitan información global, debe evaluarse si corresponde a:

```text
core/
```

El objetivo es evitar dependencias como:

```text
feature1 → feature2 → feature3 → feature4
```

y favorecer:

```text
           ┌───────────┐
           │   Core    │
           └─────┬─────┘
                 │
       ┌─────────┼─────────┐
       ▼         ▼         ▼
   feature1   feature2   feature3
       │         │         │
       └─────────┼─────────┘
                 ▼
              Shared
```

---

## 4. No duplicar componentes reutilizables

Antes de crear un componente dentro de un feature, verificar si ya existe un componente equivalente en `shared`.

Sin embargo, **no mover componentes a `shared` solamente para evitar duplicación**.

Debe existir una verdadera necesidad de reutilización.

La prioridad es mantener la cohesión del feature.

---

## 5. No colocar lógica de negocio en componentes

Los componentes deben encargarse principalmente de:

* Presentar información.
* Capturar interacción del usuario.
* Invocar servicios.
* Gestionar el estado de la vista.

La comunicación con la API debe realizarse mediante servicios.

Ejemplo:

```text
feature1Page
      │
      ▼
feature1Service
      │
      ▼
Spring Boot API
```

Evitar realizar llamadas HTTP directamente desde los componentes.

La lógica relacionada con eventos WebSocket tampoco debe implementarse directamente dentro de los componentes.

Preferir:

```text
Component
    │
    ▼
Feature Service
    │
    ▼
WebSocketService
```

---

## 6. Comunicación con el backend

Cada feature debe encapsular la comunicación relacionada con su funcionalidad.

Ejemplo:

```text
features/
└── feature1/
    └── services/
        └── feature1.service.ts
```

Este servicio será responsable de operaciones como:

```text
GET    /api/pacientes
GET    /api/pacientes/{id}
POST   /api/pacientes
PUT    /api/pacientes/{id}
PATCH  /api/pacientes/{id}/estado
```

Los componentes no deben conocer detalles innecesarios de HTTP.

### Comunicación en tiempo real

MediSistema utilizará WebSocket para aquellas funcionalidades que requieran actualización de información en tiempo real.

REST continuará siendo el mecanismo principal para:

* Consultas.
* Registros.
* Modificaciones.
* Eliminaciones.
* Operaciones CRUD.

WebSocket se utilizará únicamente para eventos que necesiten propagarse inmediatamente a otros clientes conectados.

Por ejemplo, en la gestión de citas:

```text
Secretaria
    │
    │ POST /api/citas
    ▼
Spring Boot
    │
    ├── Guarda la cita
    │
    └── Emite evento WebSocket
             │
             ▼
       WebSocketService
             │
             ▼
       Feature de citas
             │
             ▼
      Actualiza la agenda
```

Eventos posibles:

```text
cita.creada
cita.modificada
cita.cancelada
cita.estado.actualizado
```

El `WebSocketService` ubicado en `core/services` administra la conexión global.

Los features únicamente deben suscribirse a los eventos que sean relevantes para ellos.

**No crear conexiones WebSocket independientes dentro de cada feature.**

WebSocket también debe respetar la autenticación y autorización mediante JWT. Un usuario solamente podrá recibir eventos relacionados con los recursos y funcionalidades a los que tenga acceso.

---

## 7. Autenticación y JWT

La autenticación es una preocupación transversal de toda la aplicación, por lo que debe permanecer en `core`.

```text
core/
├── guards/
├── interceptors/
├── models/
└── services/
```

El JWT será gestionado mediante:

```text
core/services/auth.service.ts
```

El token será agregado automáticamente a las solicitudes mediante:

```text
core/interceptors/auth.interceptor.ts
```

El control de acceso a las rutas se realizará mediante:

```text
core/guards/auth.guard.ts
core/guards/role.guard.ts
```

Los features no deben implementar su propio mecanismo de autenticación.

La conexión WebSocket también debe respetar el mecanismo de autenticación y autorización basado en JWT.

El `WebSocketService` debe utilizar el contexto de autenticación existente y no implementar un mecanismo de autenticación independiente.

---

## 8. Control de acceso por roles

MediSistema posee los siguientes roles:

```text
ADMINISTRADOR

SECRETARIA

MEDICO
```

El acceso a las funcionalidades debe controlarse mediante guards y configuración de rutas.

Ejemplo conceptual:

```text
/usuarios
    → ADMINISTRADOR

/medicos
    → ADMINISTRADOR

/pacientes
    → SECRETARIA

/citas
    → SECRETARIA

/consultas
    → MEDICO

/expediente
    → MEDICO

/documentos
    → SECRETARIA
```

El frontend debe ocultar o bloquear las funcionalidades que no correspondan al usuario, pero la autorización real también debe ser validada por el backend mediante Spring Security y JWT.

La misma regla aplica a WebSocket: un usuario no debe recibir eventos correspondientes a información que no tenga autorización para consultar.

---

## 9. Regla general de cohesión

Antes de colocar un archivo fuera de un feature, preguntarse:

> **¿Este archivo pertenece exclusivamente a una funcionalidad?**

Si la respuesta es **sí**, debe permanecer dentro del feature.

Ejemplo:

```text
feature1Form

→ features/feature1/components/
```

Si la respuesta es **no** y es reutilizable:

```text
ConfirmationDialog

→ shared/components/
```

Si es una funcionalidad transversal:

```text
AuthService

→ core/services/

WebSocketService

→ core/services/
```

---

## 10. Regla general de acoplamiento

Evitar que un feature conozca detalles internos de otro feature.

Preferir:

```text
Feature
   │
   ▼
Service propio
   │
   ▼
API
```

Para eventos en tiempo real:

```text
Feature
   │
   ▼
WebSocketService
   │
   ▼
Spring Boot
```

en lugar de:

```text
Feature A
   │
   ├── importa componentes de Feature B
   ├── importa servicios de Feature C
   └── modifica estado interno de Feature D
```

El objetivo es que cada feature pueda modificarse o incluso eliminarse con el menor impacto posible sobre el resto de la aplicación.

---

# Objetivo arquitectónico

La generación de features debe priorizar:

1. **Alta cohesión:** los archivos relacionados con una funcionalidad deben permanecer juntos.
2. **Bajo acoplamiento:** los features deben minimizar las dependencias directas entre sí.
3. **Separación de responsabilidades:** cada capa debe tener una responsabilidad clara.
4. **Reutilización controlada:** utilizar `shared` únicamente para elementos realmente reutilizables.
5. **Centralización de preocupaciones transversales:** autenticación, JWT, guards, interceptores y WebSocket deben permanecer en `core`.
6. **Comunicación adecuada:** utilizar REST para operaciones normales y WebSocket únicamente cuando se requiera comunicación en tiempo real.
7. **Seguridad:** las comunicaciones REST y WebSocket deben respetar la autenticación y autorización basada en JWT.
8. **Escalabilidad:** la estructura debe permitir agregar nuevas funcionalidades sin convertir el proyecto en una estructura monolítica de componentes, servicios o modelos.
9. **Mantenibilidad:** cualquier desarrollador debe poder identificar rápidamente dónde se encuentra todo lo relacionado con una funcionalidad.

**Regla principal:** organizar el código por **funcionalidad**, no por tipo técnico. Cada feature debe ser lo más **cohesivo, independiente y autosuficiente** posible.