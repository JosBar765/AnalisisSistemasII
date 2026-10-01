# Corrección 9 — Limpieza de archivos huérfanos en Storage (Backend)

> Cierra el pendiente de `correcciones5.md` ("si el proceso se cae... no hay barrido periódico").

## Problema

`DocumentoServiceImpl` ya elimina el archivo recién subido si la transacción falla (`correcciones5.md`), pero
Supabase Storage **no participa en la transacción de PostgreSQL**: si el proceso se cae (o se reinicia) entre la
subida y el *commit*, el archivo queda en el bucket sin ningún documento que lo referencie. Ninguna transacción
de base de datos puede cubrir ese caso; hace falta reconciliar.

## Solución

1. **Validar antes de subir.** En `DocumentoServiceImpl.actualizar`, la categoría se valida *antes* de subir el
   archivo; así la subida es el último paso externo y los errores de validación ya no llegan a subir nada.
2. **Tarea programada** `LimpiezaArchivosHuerfanosTarea` (`services/impl`):
   - Lista todos los archivos del bucket (`AlmacenamientoService.listarArchivos()`, que recorre las carpetas
     `paciente-N/`) y arma el conjunto de rutas **referenciadas**: `Documento.url`, `AuditoriaDocumento.url_anterior`
     y `url_nuevo`. Los archivos reemplazados **no** son huérfanos: la auditoría los conserva.
   - Elimina los que no están referenciados **y** son más viejos que el período de gracia (un archivo recién
     subido puede estar en medio de su transacción). Cada borrado se registra como `WARN` con su ruta; al final
     se registra un `INFO` con el resumen.
   - Si no logra listar el bucket o consultar la base, **no borra nada** y registra un `ERROR`.
3. `@EnableScheduling` en `config/SchedulingConfig`.

## Configuración

| Propiedad (`application.properties`) | Por defecto | Significado |
| --- | --- | --- |
| `app.almacenamiento.limpieza.gracia-minutos` | 1440 (24 h) | Edad mínima de un archivo sin referencia para borrarlo |
| `app.almacenamiento.limpieza.intervalo-minutos` | 60 | Cada cuánto se ejecuta (primera ejecución tras el mismo intervalo) |

La tarea solo existe con el **perfil `prod`** (`@Profile("prod")`, `SPRING_PROFILES_ACTIVE=prod` en Railway). Es
deliberado: una base de desarrollo no conoce los archivos de otra, y si comparte el bucket con producción los
borraría.

## Archivos modificados

```
services/ArchivoAlmacenado.java                       (nuevo: ruta + fecha de creación)
services/AlmacenamientoService.java                   (+ listarArchivos)
services/impl/SupabaseAlmacenamientoServiceImpl.java  (+ listarArchivos: POST /object/list/{bucket}, paginado)
services/impl/LimpiezaArchivosHuerfanosTarea.java     (nuevo)
services/impl/DocumentoServiceImpl.java               (validar antes de subir)
repositories/DocumentoRepository.java                 (+ findAllUrls)
repositories/AuditoriaDocumentoRepository.java        (+ findAllUrlsAnteriores, findAllUrlsNuevas)
config/SchedulingConfig.java                          (nuevo)
resources/application.properties                      (propiedades de limpieza)
```

## Resultado (verificado contra Supabase real, en un bucket temporal ya eliminado)

Con perfil `prod`, gracia e intervalo de 1 minuto: se creó un documento y su reemplazo y se subió a mano un archivo
sin referencia. Primera pasada: 3 archivos revisados, 0 eliminados (dentro de la gracia). Segunda: eliminó
solo el huérfano; el archivo actual y el reemplazado se conservaron. `mvnw compile` sin errores.

## Consideraciones

- **Riesgo asumido:** la tarea borra archivos clínicos. Por eso la gracia es de 24 h, solo corre en `prod` y se
  detiene ante cualquier error. No compartir el bucket entre entornos con bases distintas.
- El nombre `gracia-minutos` admite variable de entorno (`APP_ALMACENAMIENTO_LIMPIEZA_GRACIA_MINUTOS`).
- Si se necesita una limpieza fuera de horario, basta bajar el intervalo; no hay endpoint manual.
