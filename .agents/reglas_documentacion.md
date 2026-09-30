# REGLAS DE DOCUMENTACIÓN Y CONTEXTO — MediSistema

## 1. ESTRUCTURA DE `.agents`

La carpeta `.agents` contiene toda la documentación, contexto y reglas que deben utilizarse durante el desarrollo de MediSistema.

La estructura actual es:

```text
.agents/
├── analisis/
│   └── analisis_medisistema.md
│
├── backend/
│   └── documentacion/
│
├── frontend/
│   └── documentacion/
│
├── reglas_despliegue.md
├── reglas_estructura_y_features.md
└── reglas_documentacion.md
````

Cada sección tiene una responsabilidad específica.

---

# 2. INFORMACIÓN GENERAL DEL PROYECTO

Los archivos ubicados directamente en la raíz de `.agents` contienen información y reglas generales que aplican a todo el proyecto.

```text
.agents/
├── reglas_despliegue.md
├── reglas_estructura_y_features.md
└── reglas_documentacion.md
```

### `database/schema.sql` (fuera de `.agents`)

El esquema de base de datos vive en la carpeta `database/` de la raíz del repositorio
(`database/schema.sql` y `database/seed.sql`), no en `.agents`. Antes se llamaba
`.agents/MediSistema.sql`; los documentos históricos de `backend/documentacion/` que lo mencionan se
refieren a `database/schema.sql`, y `docker/init/02_seed.sql` pasó a ser `database/seed.sql`.

`database/schema.sql` contiene el esquema de base de datos de MediSistema.

Debe utilizarse como referencia para conocer:

* Tablas.
* Columnas.
* Claves primarias.
* Claves foráneas.
* Relaciones.
* Restricciones.
* Índices.
* Estructura general de PostgreSQL.

No se deben inventar entidades o relaciones que no estén definidas en el modelo, salvo que el análisis del sistema indique explícitamente la necesidad de realizar un cambio.

---

### `reglas_despliegue.md`

Contiene las reglas relacionadas con la arquitectura de despliegue de MediSistema.

Define principalmente:

* Railway.
* Spring Boot.
* Angular.
* Supabase.
* PostgreSQL.
* Supabase Storage.
* JWT.
* Comunicación entre Frontend y Backend.
* Variables de entorno.
* CORS.
* Seguridad.

Estas reglas deben respetarse cuando se realicen cambios relacionados con infraestructura, configuración o despliegue.

---

### `reglas_estructura_y_features.md`

Contiene las reglas de arquitectura y organización del código, especialmente para el Frontend Angular.

Define principios como:

* Alta cohesión.
* Bajo acoplamiento.
* Organización por funcionalidades.
* Estructura de features.
* Uso de `core`.
* Uso de `shared`.
* Uso de `layouts`.
* Separación de responsabilidades.

Antes de crear o modificar componentes, servicios, modelos o features, se debe consultar este documento.

---

### `reglas_documentacion.md`

Este documento define las reglas que deben seguirse para generar y mantener la documentación del desarrollo.

---

# 3. ANÁLISIS DEL SISTEMA

La carpeta:

```text
.agents/analisis/
```

contendrá el análisis funcional general de MediSistema y correcciones que se irán guionizando a lo largo del desarrollo del sistema.

Su estructura será:

```text
.agents/
└── analisis/
    └── analisis_medisistema.md
```

Posteriormente podría ser:
```text
.agents/
└── analisis/
    └── analisis_correcciones_1.md
    └── analisis_correcciones_2.md
```

El archivo:

```text
analisis_medisistema.md
```

contendrá la descripción general del sistema y servirá como fuente principal de contexto funcional.

Este documento permitirá conocer:

* Qué es MediSistema.
* Objetivo del sistema.
* Alcance.
* Actores.
* Roles.
* Funcionalidades.
* Reglas de negocio.
* Casos de uso.
* Procesos principales.
* Restricciones.
* Consideraciones funcionales.

---

# 4. REGLA PARA EL USO DEL ANÁLISIS

Antes de implementar una funcionalidad nueva o realizar cambios importantes, se debe consultar:

```text
.agents/analisis/analisis_medisistema.md
```

El análisis debe utilizarse para comprender **qué debe hacer el sistema**.

Las reglas de arquitectura deben utilizarse para determinar **cómo debe implementarse**.

Por lo tanto:

```text
Análisis
   │
   │ Define QUÉ debe hacer el sistema
   ▼
Implementación
   │
   │ Respeta las reglas de arquitectura
   ▼
Código
```

No se debe modificar el comportamiento funcional del sistema basándose únicamente en suposiciones.

Si una funcionalidad no está claramente definida en el análisis, se debe evitar inventar reglas de negocio.

---

# 5. DOCUMENTACIÓN DEL BACKEND

Toda modificación relacionada con el Backend debe documentarse dentro de:

```text
.agents/backend/documentacion/
```

Ejemplo:

```text
.agents/backend/documentacion/
├── creacion.md
├── correcciones1.md
├── correcciones2.md
└── ...
```

La documentación debe registrar los cambios realizados en el Backend.

Esto puede incluir:

* Creación de entidades.
* Creación de DTOs.
* Creación de mappers.
* Creación de repositories.
* Creación de services.
* Creación de controllers.
* Implementación de endpoints.
* Implementación de seguridad.
* Implementación de JWT.
* Validaciones.
* Manejo de excepciones.
* Cambios en la configuración.
* Correcciones de errores.
* Refactorizaciones importantes.

---

# 6. DOCUMENTACIÓN DEL FRONTEND

Toda modificación relacionada con Angular debe documentarse dentro de:

```text
.agents/frontend/documentacion/
```

Ejemplo:

```text
.agents/frontend/documentacion/
├── creacion.md
├── correcciones1.md
├── correcciones2.md
└── ...
```

La documentación puede incluir:

* Creación de features.
* Creación de páginas.
* Creación de componentes.
* Creación de servicios.
* Creación de modelos.
* Configuración de rutas.
* Guards.
* Interceptors.
* Formularios.
* Integración con la API.
* Implementación de JWT.
* Cambios de interfaz.
* Correcciones.
* Refactorizaciones.

---

# 7. NOMENCLATURA DE DOCUMENTOS

Las modificaciones deben documentarse mediante archivos enumerados.

La primera implementación general puede documentarse como:

```text
creacion.md
```

Las modificaciones posteriores deben utilizar numeración:

```text
correcciones1.md
correcciones2.md
correcciones3.md
...
```

Ejemplo:

```text
backend/documentacion/
├── creacion.md
├── correcciones1.md
├── correcciones2.md
└── correcciones3.md
```

Y:

```text
frontend/documentacion/
├── creacion.md
├── correcciones1.md
├── correcciones2.md
└── correcciones3.md
```

La numeración debe ser incremental.

No sobrescribir una documentación anterior para registrar cambios nuevos.

---

# 8. REGLA DE TRAZABILIDAD

Cada cambio importante realizado en el código debe poder relacionarse con una documentación correspondiente.

Ejemplo:

```text
Creación inicial del módulo de pacientes
        │
        ▼
frontend/documentacion/creacion.md
```

Posteriormente:

```text
Corrección de validación del DPI
        │
        ▼
frontend/documentacion/correcciones1.md
```

Y posteriormente:

```text
Corrección de integración con API
        │
        ▼
frontend/documentacion/correcciones2.md
```

Esto permite mantener un historial de evolución del proyecto.

---

# 9. CONTENIDO MÍNIMO DE CADA DOCUMENTACIÓN

Cada archivo de documentación debe indicar como mínimo:

## Objetivo

Explicar qué se implementó o modificó.

## Archivos afectados

Indicar los archivos creados o modificados.

Ejemplo:

```text
frontend/src/app/features/pacientes/
├── pages/
├── components/
├── services/
└── models/
```

## Cambios realizados

Explicar brevemente qué se hizo.

## Integración

Indicar cómo se relaciona el cambio con otras partes del sistema.

Por ejemplo:

```text
PacientePage
    │
    ▼
PacienteService
    │
    ▼
Spring Boot API
    │
    ▼
PostgreSQL
```

## Consideraciones

Indicar cualquier aspecto importante que deba conocerse para futuras modificaciones.

---

# 10. DOCUMENTACIÓN DE UNA CREACIÓN

Cuando se cree una funcionalidad desde cero, utilizar:

```text
creacion.md
```

El documento debe explicar la implementación inicial.

Ejemplo:

```markdown
# Creación — Feature de Pacientes

## Objetivo

Implementar la gestión de pacientes en Angular.

## Archivos creados

- ...
- ...
- ...

## Funcionalidades

- Registrar paciente.
- Consultar paciente.
- Modificar paciente.
- Activar/Inactivar paciente.
- Registrar signos vitales.

## Integración

El feature consume los endpoints correspondientes de la API
Spring Boot mediante `PacienteService`.

## Consideraciones

El feature debe mantener alta cohesión y bajo acoplamiento.
```

---

# 11. DOCUMENTACIÓN DE CORRECCIONES

Cuando se modifique una funcionalidad existente, crear un nuevo documento.

Ejemplo:

```text
correcciones1.md
```

No modificar:

```text
creacion.md
```

para registrar la nueva corrección.

Ejemplo:

```markdown
# Corrección 1 — Feature de Pacientes

## Problema

La validación del DPI permitía valores con una longitud incorrecta.

## Solución

Se agregó una validación para garantizar que el DPI
cumpla con el formato esperado.

## Archivos modificados

- paciente-form.component.ts

## Resultado

El formulario ahora rechaza valores inválidos antes
de enviarlos al Backend.
```

---

# 12. NO GENERAR DOCUMENTACIÓN INNECESARIA

No se debe crear un archivo de documentación por cada pequeño cambio trivial.

La documentación debe generarse cuando exista una modificación significativa, por ejemplo:

* Nueva funcionalidad.
* Nuevo feature.
* Nuevo endpoint.
* Cambio estructural.
* Cambio de arquitectura.
* Corrección importante.
* Cambio en seguridad.
* Cambio en integración con servicios externos.
* Cambio importante en base de datos.

Cambios puramente cosméticos o internos que no tengan relevancia arquitectónica pueden documentarse junto con otro cambio relacionado.

---

# 13. REGLA DE SEPARACIÓN BACKEND / FRONTEND

La documentación debe permanecer separada según el componente afectado.

Si el cambio solamente afecta al Backend:

```text
.agents/backend/documentacion/
```

Si solamente afecta al Frontend:

```text
.agents/frontend/documentacion/
```

Si un cambio afecta a ambos:

```text
.agents/backend/documentacion/
.agents/frontend/documentacion/
```

Cada lado debe documentar únicamente los cambios correspondientes a su propia aplicación.

---

# 14. ORDEN DE CONSULTA DEL CONTEXTO

Antes de implementar una funcionalidad, seguir este orden conceptual:

```text
1. Analizar analisis/analisis_medisistema.md
                │
                ▼
2. Revisar reglas generales
                │
                ├── reglas_despliegue.md
                └── reglas_estructura_y_features.md
                │
                ▼
3. Revisar database/schema.sql
                │
                ▼
4. Revisar documentación existente
                │
                ▼
5. Implementar
                │
                ▼
6. Documentar cambios
```

No se debe asumir que la documentación anterior está desactualizada sin verificar el código actual.

El código existente es la fuente de verdad sobre el estado actual de la implementación, mientras que los documentos `.agents` proporcionan el contexto, las reglas y el historial de decisiones.

---

# 15. OBJETIVO GENERAL

La carpeta `.agents` debe permitir que cualquier agente de desarrollo pueda comprender MediSistema sin necesidad de reconstruir el contexto desde cero.

La estructura debe mantener separadas tres responsabilidades:

```text
┌─────────────────────────────┐
│           ANÁLISIS          │
│                             │
│ ¿Qué debe hacer el sistema? │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│           REGLAS            │
│                             │
│ ¿Cómo debe desarrollarse?   │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│       IMPLEMENTACIÓN        │
│                             │
│ ¿Qué se ha desarrollado?    │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│       DOCUMENTACIÓN         │
│                             │
│ ¿Qué cambios se realizaron? │
└─────────────────────────────┘
```

La prioridad durante todo el desarrollo es mantener:

* **Alta cohesión.**
* **Bajo acoplamiento.**
* **Separación de responsabilidades.**
* **Trazabilidad de cambios.**
* **Consistencia con el análisis funcional.**
* **Consistencia con el modelo de datos.**
* **Documentación clara y acumulativa.**
