# Corrección 12 — Errores de base de datos y de validación explicativos (Backend)

## Problema

Ante cualquier error de PostgreSQL, `GlobalExceptionHandler` respondía el mismo texto genérico
("La operación viola una restricción de integridad de datos (referencia inexistente o duplicada)"), sin decir la
causa. Caso reportado: crear dos usuarios con el mismo teléfono. Además:

- El frontend de administración ignoraba el mensaje del Backend y mostraba textos fijos
  ("Verifique los datos ingresados").
- Los DTO de administración (`Usuario`, `Medico`, `Jornada`, catálogos) no tenían validación ni `@Valid`: un dato
  vacío o demasiado largo llegaba hasta la base.
- Los mensajes de validación de `Paciente` y `Cita` salían en inglés ("must not be blank") con nombres técnicos
  de campo (`primerNombre`).

## Mapa de restricciones de la base (`database/schema.sql`, verificado en la base viva)

| Tipo | Restricciones |
| --- | --- |
| **Únicas** | `Usuario.correo`, `Usuario.telefono`, `Paciente.dpi`, `Consulta.id_cita`; claves primarias (`Medico.id` = `Usuario.id`, por lo que un usuario solo puede ser médico una vez) |
| **Claves foráneas** | 19, **todas `ON DELETE NO ACTION`**: no se puede eliminar un registro referenciado (p. ej. una categoría de documento con documentos, una especialidad con médicos) |
| **NOT NULL** | todas las columnas salvo `segundo_nombre` (Usuario y Paciente), `url_anterior`/`url_nuevo` (AuditoriaDocumento) y las de `Cita` y `AuditoriaConsulta` añadidas después |
| **Longitudes** | `telefono` 15, `colegiado` 15, `dpi` 13, nombres/apellidos 50 (segundo nombre 100), `correo` y `Documento.nombre` 255, `direccion` 250, catálogos 100 (`Especialidad`, `CategoriaDocumento`) |
| **CHECK** | ninguno |
| **Lo que la base NO impide** | citas duplicadas en un mismo médico/fecha/hora y jornadas que se traslapan (solo lo valida `CitaServiceImpl`); un usuario con rol que no es médico registrado como médico; correos que solo difieren en mayúsculas (la unicidad distingue mayúsculas); `Paciente.telefono` repetido (intencional, ver comentario del esquema) |

## Solución

1. **`exceptions/MensajeIntegridadDatos`** traduce el error de PostgreSQL (`PSQLException`: SQLState, constraint,
   columna, detalle) a una explicación:

   | Causa | Estado | Ejemplo de mensaje |
   | --- | --- | --- |
   | Valor único repetido (23505) | **409** | "Ya existe un usuario registrado con el número de teléfono «70000001»." / "…con el correo electrónico «x»." / "…con el DPI «x»." / "La cita ya tiene una consulta registrada." / "El usuario ya está registrado como médico." |
   | Registro referenciado inexistente (23503, alta o cambio) | 400 | "La especialidad indicada (id 99) no existe." |
   | Eliminar algo en uso (23503, baja) | **409** | "No se puede eliminar o modificar el registro porque está siendo utilizado por: documentos." |
   | Campo obligatorio vacío (23502) | 400 | "El campo «primer nombre» es obligatorio." |
   | Texto demasiado largo (22001) / número fuera de rango (22003) | 400 | "Uno de los textos supera la longitud máxima permitida (máximo 15 caracteres)." |
   | Cualquier otra | 400 | mensaje genérico (el detalle técnico se registra como `WARN` en el log) |

   Una restricción única nueva sin entrada en `MensajeIntegridadDatos.UNICAS` igual se explica con el nombre de la
   columna y el valor ("Ya existe un registro con el mismo valor en «x»: «y»."). Para mensajes más específicos,
   agregar su nombre de restricción al mapa.
2. **Validación de entrada** (antes de llegar a la base): `UsuarioRequestDTO`, `MedicoRequestDTO` y
   `CatalogoRequestDTO` con `@Size` según las columnas, `@Email` y "no vacío" (`@Pattern`); `@Valid` en los
   controllers de usuarios, médicos, jornadas, especialidades y categorías. El `PUT` sigue admitiendo
   actualizaciones parciales, por eso lo obligatorio **al crear** se exige en el servicio con
   `CamposObligatorios.exigir` (usuario: rol, nombres, correo, teléfono, contraseña; médico: usuario,
   especialidad, colegiado; jornada: médico, día, horas, duración). La contraseña admite hasta 72 caracteres
   (límite de BCrypt) y `Documento.nombre` hasta 255.
3. **Idioma de la validación:** `spring.web.locale=es` + `locale-resolver=fixed` (mensajes de Bean Validation en
   español) y `handleValidation` ya no antepone el nombre técnico: un mensaje propio se muestra tal cual y uno
   por defecto como "El campo «primer nombre» no debe estar vacío.". Varios errores se unen con espacios.
4. **`pom.xml`:** el driver `postgresql` pasó de `runtime` a ámbito normal para poder usar `PSQLException`.

## Archivos modificados

```
exceptions/MensajeIntegridadDatos.java (nuevo), exceptions/GlobalExceptionHandler.java
services/impl/CamposObligatorios.java (nuevo), UsuarioServiceImpl, MedicoServiceImpl, JornadaMedicaServiceImpl, DocumentoServiceImpl
domain/dtos/usuario/UsuarioRequestDTO, medico/MedicoRequestDTO, catalogo/CatalogoRequestDTO
controllers/admin/{Usuario,Medico,JornadaMedica,Especialidad,CategoriaDocumento}Controller   (@Valid)
resources/application.properties (idioma), pom.xml
```

## Resultado (verificado con el Backend en ejecución)

Mismo teléfono o correo al crear **y al actualizar** → 409 con el valor; médico duplicado → 409; rol o
especialidad inexistente → 400 con su nombre; campos vacíos, en blanco, correo mal formado, teléfono de 20
dígitos, colegiado de 40, nombre de especialidad de 150 → 400 explicando el campo; eliminar una categoría con
documentos → 409 "utilizado por: documentos". Antes: todos 400 con el mensaje genérico. `mvnw compile` sin errores.

## Consideraciones

- Los códigos cambian de 400 a **409** para duplicados y registros en uso; los clientes que miraban el 400
  (solo `especialidades.component`, ya actualizado) deben leer `message`.
- `SQLSTATE 22001` no indica la columna: por eso las longitudes se validan antes en los DTO; el mensaje de la
  base es el último recurso.
- Los mensajes propios son siempre en español; los por defecto de Bean Validation siguen el idioma fijo `es`.
- **Resuelto después:** jornadas que se traslapan → `correcciones13.md`; usuario del médico con rol `MEDICO` (servicio y
  base) → `correcciones14.md`; correos y nombres que difieren en mayúsculas → `correcciones15.md`.
