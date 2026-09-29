# FLUJO DE NEGOCIO DE MEDISISTEMA

## 1. ADMINISTRACIÓN DEL SISTEMA

### UC-ADM-001 — Gestionar usuarios

El administrador puede gestionar las cuentas de usuario del sistema.

```text
Administrador
    -> Gestionar usuarios
        -> Registrar usuario
        -> Consultar usuario
        -> Modificar información
        -> Cambiar contraseña
        -> Activar/Inactivar usuario
        -> Asignar rol
```

Los roles definidos son:

```text
Administrador
Secretaria
Médico
```

El estado del usuario determina si puede acceder al sistema.

---

### UC-ADM-002 — Gestionar especialidades

```text
Administrador
    -> Gestionar especialidades
        -> Registrar especialidad
        -> Editar especialidad
        -> Eliminar especialidad
```

**Regla de negocio:**

```text
Si existen médicos asociados a una especialidad
    -> NO se permite eliminar la especialidad.
```

---

### UC-ADM-003 — Gestionar médicos

```text
Administrador
    -> Gestionar médicos
        -> Registrar médico
        -> Editar información profesional
        -> Asignar especialidad
        -> Activar/Inactivar médico
```

**Reglas de negocio:**

```text
Un médico pertenece a una única especialidad.

Un médico debe estar asociado a un usuario del sistema.

Un médico inactivo no debe utilizar el sistema ni recibir nuevas citas.
```

---

### UC-ADM-004 — Gestionar jornada médica

El administrador configura la jornada habitual de atención de cada médico.

```text
Administrador
    -> Gestionar jornada médica
        -> Seleccionar médico
        -> Seleccionar día de la semana
        -> Establecer hora de inicio
        -> Establecer hora de finalización
        -> Establecer duración estimada de consulta
```

Una misma jornada puede dividirse en varios períodos para un mismo día.

Ejemplo:

```text
Dr. López
    -> Lunes
        -> 10:00 - 13:00
        -> 14:00 - 17:00
```

Esto permite representar períodos de descanso sin crear una entidad específica para ellos.

**Reglas de negocio:**

```text
La jornada define el período habitual en el que pueden programarse citas.

La duración de consulta determina los intervalos estimados disponibles.

Los horarios disponibles se obtienen considerando la jornada configurada
y las citas existentes.

La hora asignada a una cita representa un horario estimado y NO determina
el orden real de atención.

La cola de atención se determina según la disponibilidad real de los
pacientes presentes durante el día.
```

---

### UC-ADM-005 — Consultar dashboard

El administrador puede consultar indicadores generales de la clínica:

```text
Administrador
    -> Consultar dashboard
        -> Pacientes registrados
        -> Consultas realizadas
        -> Citas canceladas
        -> Consultas por médico
        -> Otros indicadores definidos
```

---

# 2. GESTIÓN DE PACIENTES

## UC-SEC-001 — Gestionar pacientes

La secretaria administra la información administrativa de los pacientes.

El proceso puede comenzar cuando un paciente solicita una cita mediante:

```text
Paciente
    -> Teléfono / WhatsApp / Presencial
    -> Secretaria
```

La secretaria busca al paciente.

```text
Buscar paciente
    -> ¿Existe?
        -> NO -> Registrar paciente
        -> SÍ -> Continuar
```

### Funciones

```text
Gestionar pacientes
    -> Registrar paciente
        -> Registrar información personal

    -> Consultar paciente

    -> Buscar paciente

    -> Modificar información del paciente

    -> Activar/Inactivar paciente
```

**Regla de negocio:**

```text
La secretaria administra información administrativa del paciente,
pero no modifica información clínica.
```

---

# 3. GESTIÓN DE DOCUMENTOS CLÍNICOS

## UC-SEC-002 — Gestionar documentos

Los documentos pertenecen directamente al paciente y forman parte de su expediente clínico.

Pueden provenir de:

```text
Otros hospitales
Laboratorios
Otros médicos
Clínicas externas
El propio paciente
```

Ejemplos:

```text
Exámenes de laboratorio
Radiografías
Resonancias
Recetas
Referencias médicas
Otros documentos clínicos
```

### Carga de documentos

```text
Secretaria
    -> Gestionar documentos
        -> Subir documento
        -> Seleccionar categoría
        -> Asociar documento al paciente
```

Las categorías disponibles son administradas por el administrador.

```text
Administrador
    -> Gestiona catálogo de categorías
```

### Actualización de documentos

Si el archivo asociado es incorrecto o debe ser reemplazado:

```text
Secretaria
    -> Actualizar documento
        -> Seleccionar documento
        -> Subir nuevo archivo
        -> Seleccionar categoría si corresponde
        -> Indicar motivo
        -> Guardar actualización
```

El sistema:

```text
Genera nueva URL
    -> Actualiza URL del documento
    -> Registra usuario
    -> Registra fecha y hora
    -> Registra motivo
    -> Registra información anterior
    -> Registra información nueva
    -> Genera auditoría
```

**Reglas de negocio:**

```text
Los documentos pertenecen al paciente, no a una consulta específica.

El archivo anterior no se elimina automáticamente del almacenamiento.

El sistema deja de utilizar la URL anterior como referencia activa.

Toda actualización de un documento genera un registro de auditoría.

La secretaria puede administrar documentos, pero no modificar información clínica.
```

---

# 4. GESTIÓN DE CITAS

## UC-SEC-003 — Gestionar citas

La secretaria administra las citas de los pacientes.

### Programación de una cita

```text
Secretaria
    -> Buscar paciente
        -> ¿Existe?
            -> NO -> Registrar paciente
            -> SÍ -> Continuar

    -> ¿Existen documentos médicos relevantes?
        -> SÍ -> Gestionar documentos
        -> NO -> Continuar

    -> Seleccionar médico
    -> Seleccionar fecha
    -> Consultar horarios disponibles
    -> Seleccionar horario disponible
    -> Registrar cita
```

El sistema determina los horarios disponibles considerando:

```text
Jornada del médico
    +
Duración estimada de consulta
    +
Citas existentes
```

La cita queda registrada como:

```text
Estado = "En espera"
```

### Funciones

```text
Gestionar citas
    -> Programar cita
    -> Reprogramar cita
    -> Cancelar cita
    -> Consultar agenda diaria
    -> Consultar agenda por médico
```

### Reglas de negocio

```text
Una cita pertenece a un paciente y a un médico.

Una cita tiene una fecha y una hora estimada.

La hora de la cita NO garantiza el orden de atención.

El orden real de atención funciona mediante una cola diaria.

Un paciente que se encuentre disponible antes puede ser atendido antes
que otro paciente cuya hora estimada sea anterior.

La secretaria es responsable de administrar las citas.
```

---

# 5. ATENCIÓN MÉDICA

## UC-MED-001 — Consultar agenda médica

El médico consulta únicamente sus propias citas.

```text
Médico
    -> Consultar agenda médica
        -> Consultar citas del día
        -> Consultar citas por fecha
        -> Visualizar información básica del paciente
```

Preferentemente la agenda se presenta mediante una vista de calendario o lista.

El médico visualiza la cola de pacientes del día.

```text
Médico
    -> Consulta cola
    -> Decide siguiente paciente
    -> Solicita a secretaria llamar al paciente
```

**Regla de negocio:**

```text
El médico NO llama directamente al paciente desde el sistema.

La secretaria es quien realiza la llamada cuando el médico solicita
que se atienda al siguiente paciente.
```

---

# 6. EXPEDIENTE CLÍNICO

## UC-MED-002 — Consultar expediente clínico

El expediente clínico **no constituye una entidad física independiente**.

Es una vista lógica que consolida la información clínica relacionada con un paciente.

El médico puede acceder:

```text
Desde una cita
    O
Desde el buscador de pacientes
```

El expediente presenta:

```text
Datos generales del paciente
        +
Historial de consultas
        +
Diagnósticos anteriores
        +
Tratamientos anteriores
        +
Signos vitales
        +
Documentos clínicos
```

El médico puede:

```text
Consultar historial
Consultar diagnósticos
Consultar tratamientos
Consultar signos vitales
Consultar documentos
Visualizar documentos
Descargar documentos
```

**Regla de negocio:**

```text
El médico puede consultar el expediente de cualquier paciente
cuando exista una necesidad médica.

El acceso al expediente no implica que el médico pueda modificar
información clínica histórica directamente.
```

---

# 7. REGISTRO DE CONSULTA MÉDICA

## UC-MED-003 — Registrar consulta médica

Cuando el médico inicia la atención:

```text
Médico
    -> Abre expediente
    -> Consulta antecedentes
    -> Inicia consulta
```

Durante la consulta registra:

```text
Motivo de consulta
Signos vitales
Diagnóstico
Tratamiento
Observaciones médicas
```

Los signos vitales considerados son:

```text
Peso
Altura
Presión sistólica
Presión diastólica
Temperatura
```

Al finalizar:

```text
Médico
    -> Finalizar consulta
        -> Guardar consulta
        -> Asociar consulta al paciente
        -> Agregar consulta al historial clínico
        -> Cambiar cita a "Atendido"
```

**Regla de negocio:**

```text
Solo el médico puede registrar información clínica.

Una consulta finalizada no puede eliminarse físicamente.
```

---

# 8. MODIFICACIÓN DE CONSULTA

## UC-MED-004 — Modificar consulta médica

Después de finalizar una consulta, el médico puede detectar un error que necesite corregirse.

```text
Médico
    -> Consultar consulta
    -> Modificar información
```

Puede modificar:

```text
Motivo de consulta
Signos vitales
Diagnóstico
Tratamiento
Observaciones
```

Antes de aplicar la modificación, el sistema registra la información anterior.

```text
Modificar consulta
    -> Registrar información anterior
    -> Registrar información nueva
    -> Registrar médico
    -> Registrar fecha y hora
    -> Registrar motivo de modificación
    -> Aplicar modificación
    -> Generar auditoría
```

La información anterior **no se elimina del historial de auditoría**.

---

# 9. ESTADOS DE LAS CITAS

Las citas utilizan los siguientes estados:

```text
En espera
Atendido
Cancelado
```

Flujo normal:

```text
Programar cita
    ↓
En espera
    ↓
Atención médica
    ↓
Atendido
```

Flujo alternativo:

```text
En espera
    ↓
Cancelación
    ↓
Cancelado
```

No existe un proceso independiente de `Check-in`.

La secretaria determina qué pacientes se encuentran presentes en la clínica y el médico utiliza esa información para decidir el orden de atención.

---

# 10. REGLAS GENERALES DEL SISTEMA

```text
1. Un médico pertenece a una única especialidad.

2. Una especialidad no puede eliminarse mientras tenga médicos asociados.

3. Los médicos están asociados a usuarios del sistema.

4. Los documentos pertenecen al paciente y no a una consulta específica.

5. El expediente clínico es una vista lógica de la información del paciente.

6. Solo el médico puede registrar o modificar información clínica.

7. La secretaria administra pacientes, citas y documentos.

8. El administrador administra usuarios, médicos, especialidades,
   jornadas y configuración general.

9. Toda modificación de documentos genera auditoría.

10. Toda modificación de consultas genera auditoría.

11. Las consultas no se eliminan físicamente.

12. Los documentos anteriores no se eliminan automáticamente durante
    una actualización.

13. La jornada médica determina los períodos disponibles para programar citas.

14. La duración de consulta determina los intervalos estimados.

15. La hora asignada a una cita es un horario estimado.

16. La atención real funciona mediante una cola diaria.

17. El orden de la cola puede diferir del horario originalmente asignado.

18. La secretaria es quien llama al paciente cuando el médico solicita
    su ingreso.

19. Un usuario inactivo no puede acceder al sistema.

20. Un médico inactivo no debe recibir nuevas citas.
```