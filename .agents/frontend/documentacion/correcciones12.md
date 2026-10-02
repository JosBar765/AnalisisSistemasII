# Corrección 12 — Pantalla para administrar las categorías de documentos clínicos (Frontend)

## Problema

Las categorías de documentos (Examen de laboratorio, Radiografía, ...) solo se podían crear llamando a la API
directamente: el Backend tenía `POST/PUT/DELETE /categorias-documento` (solo ADMINISTRADOR) pero no existía
pantalla.

## Solución

Nueva pestaña **"Categorías de Documentos"** en el menú del administrador (entre Especialidades y Médicos), ruta
`/admin/categorias-documento`, con el mismo diseño que Especialidades:

- Tabla con las categorías, botón **Nueva Categoría**, y **Editar** / **Eliminar** por fila.
- Modal con un campo de nombre (obligatorio, máximo 100 caracteres, igual que la columna).
- Los errores del Backend salen en el aviso emergente: p. ej. eliminar una categoría con documentos responde "No se
  puede eliminar o modificar el registro porque está siendo utilizado por: documentos." (409).
- El listado usa `GET /catalogos/categorias-documento`; los cambios llegan al instante al combo "Categoría del
  Documento" de la secretaria.

## Archivos

```
features/admin/pages/categorias-documento/categorias-documento.component.{ts,html} (nuevo)
features/admin/services/categoria-documento.service.ts (nuevo)
features/admin/services/catalogo.service.ts      (+ listarCategoriasDocumento)
features/admin/admin.routes.ts                   (ruta)
layouts/main/main-layout.component.html          (entrada de menú)
```

## Consideraciones

- No se modificó el Backend: ya guardaba los nombres en minúsculas y los mostraba en TitleCase
  (`correcciones15.md`), por eso "informe quirúrgico prueba" se ve "Informe Quirúrgico Prueba".
- TitleCase capitaliza también las partículas: la categoría del seed se muestra "Examen De Laboratorio".
- Verificado en el navegador: la pestaña aparece, lista las 6 categorías y se creó una nueva desde el modal (la de
  prueba se eliminó después).
