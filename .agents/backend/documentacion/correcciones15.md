# Corrección 15 — Textos de identidad en minúsculas al guardar y TitleCase al mostrar (Backend)

> Cierra el pendiente de `correcciones12.md` ("correos que solo difieren en mayúsculas").

## Problema

La unicidad de `Usuario.correo` distingue mayúsculas: `Ana@x.com` y `ana@x.com` eran dos correos distintos, y el mismo
nombre podía quedar guardado con capitalizaciones distintas.

## Decisión de alcance (aprobada por el usuario)

Aplica a **identidad y catálogos editables**; **no** al texto clínico libre (motivo, diagnóstico, tratamiento,
observaciones), porque en minúsculas y TitleCase se deformarían dosis, unidades y siglas ("50mg" → "50Mg", "CIE-10",
"mmHg").

| Se normaliza (minúsculas al guardar, TitleCase al mostrar) | Se guarda en minúsculas, **se muestra en minúsculas** | **No** se toca |
| --- | --- | --- |
| `Usuario`: nombres y apellidos; `Paciente`: nombres, apellidos y dirección; `Especialidad.especialidad`; `CategoriaDocumento.categoria_documento` | `Usuario.correo`, `Paciente.correo` | contraseñas, nombres de archivo y rutas de Storage, `DPI`, teléfono, colegiado, texto clínico, catálogos fijos (`Rol`, `EstadoCita`, `DiaSemana`, motivos de modificación: el código compara sus nombres) |

## Solución

- **Guardar:** anotación `@Minusculas` (`domain/normalizacion`) en los campos de las 4 entidades y un listener JPA
  `NormalizadorTextos` (`@EntityListeners`, `@PrePersist` y `@PreUpdate`) que recorta (`trim`) y pasa a minúsculas
  esos campos antes de insertar o actualizar. Al estar en la entidad, ninguna ruta de escritura puede saltárselo; y
  la unicidad del correo (y de cualquier valor) ya no depende de mayúsculas.
- **Mostrar:** `utils/Texto.titulo(...)` (primera letra de cada palabra en mayúscula; separadores: espacio, guion,
  apóstrofo y punto; una letra tras un dígito no se capitaliza: "5a. avenida" → "5a. Avenida") aplicado en las
  respuestas: `PacienteMapper`, `UsuarioMapper`, `CatalogoMapper` (especialidades y categorías), nombres compuestos
  en `ConsultaMapper` (`nombreMedico`, `nombrePaciente`), `AuditoriaConsultaMapper` y `AuditoriaDocumentoMapper`
  (`nombreUsuario`), el claim `nombre` del JWT y el nombre del médico en el dashboard. El **frontend no cambió**: ya
  mostraba lo que devuelve la API. Las partículas ("de", "la") también se capitalizan ("De La Cruz").
- **Login:** `AuthServiceImpl` busca el correo en minúsculas, así que `PEPE@TEST.COM` inicia sesión.
- **Datos iniciales:** `database/seed.sql` guarda en minúsculas las especialidades, categorías y el administrador
  (nombre `administrador sistema medisistema`).

## Archivos modificados

```
utils/Texto.java, domain/normalizacion/{Minusculas,NormalizadorTextos}.java (nuevos)
domain/entities/{Usuario,Paciente,Especialidad,CategoriaDocumento}Entity.java
mappers/impl/{Paciente,Usuario,Catalogo,Consulta,AuditoriaConsulta,AuditoriaDocumento}Mapper.java
security/JwtService.java, services/impl/{AuthServiceImpl,DashboardServiceImpl}.java
database/seed.sql
```

## Base de datos existente

Las filas ya guardadas con mayúsculas deben pasarse a minúsculas (en la base local ya se hizo):

```sql
UPDATE "Usuario" SET primer_nombre=lower(trim(primer_nombre)), segundo_nombre=lower(trim(segundo_nombre)),
  primer_apellido=lower(trim(primer_apellido)), segundo_apellido=lower(trim(segundo_apellido)), correo=lower(trim(correo));
UPDATE "Paciente" SET primer_nombre=lower(trim(primer_nombre)), segundo_nombre=lower(trim(segundo_nombre)),
  primer_apellido=lower(trim(primer_apellido)), segundo_apellido=lower(trim(segundo_apellido)),
  correo=lower(trim(correo)), direccion=lower(trim(direccion));
UPDATE "Especialidad" SET especialidad=lower(trim(especialidad));
UPDATE "CategoriaDocumento" SET categoria_documento=lower(trim(categoria_documento));
```
Si hubiera correos que solo difieren en mayúsculas, el `UPDATE` de `Usuario` fallará por la restricción única.

## Resultado (verificado con el Backend en ejecución)

Usuario enviado como `"  JOSÉ  "`, `LUIS`, `pérez`, `DE LA CRUZ`, `Pepe@Test.COM`: la respuesta muestra
`José | Luis | Pérez | De La Cruz | pepe@test.com` y la base guarda `josé`, `luis`, `pérez`, `de la cruz`,
`pepe@test.com`. Crear otro usuario con `PEPE@test.com` → 409; login con `PEPE@TEST.COM` → 200; especialidad
`CARDIO VASCULAR` → guardada `cardio vascular`, mostrada `Cardio Vascular`; paciente con dirección
`ZONA 10, 5A. AVENIDA 12-34` → guardada en minúsculas y mostrada `Zona 10, 5a. Avenida 12-34`.

## Consideraciones

- **Los formularios de edición** reciben el texto en TitleCase y lo reenvían; se vuelve a guardar en minúsculas, sin
  pérdida.
- **La base no impone las minúsculas** (no hay `CHECK`): un `INSERT` por SQL con mayúsculas se guardaría tal cual. Si se
  quiere garantía total en la base: `CHECK (correo = lower(correo))` o un índice único sobre `lower(correo)`.
- TitleCase no sabe de apellidos como "McDonald" o "van der Berg"; se muestran "Mcdonald" y "Van Der Berg".
- Una dirección guardada en minúsculas se muestra en TitleCase aunque contenga siglas ("zona 10 uvg" → "Zona 10 Uvg").
