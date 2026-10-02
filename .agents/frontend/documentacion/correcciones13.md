# Corrección 13 — DICOM fuera de los formatos de subida (Frontend)

> Acompaña a `backend/documentacion/correcciones17.md`.

## Problema

El modal "Subir Documento" y el de reemplazo ofrecían archivos `.dcm` (DICOM, formato de imágenes médicas), que el
bucket no admite.

## Solución

- `documento.model.ts`: `EXTENSIONES_PERMITIDAS` pasa de `.pdf,.jpg,.jpeg,.png,.dcm` a `.pdf,.jpg,.jpeg,.png`. Esa
  constante alimenta el atributo `accept` de los dos selectores de archivo, así que el diálogo del sistema ya no
  ofrece `.dcm`.
- La etiqueta del modal de subida ahora dice "Seleccionar Archivo (PDF, JPG, PNG)".

## Archivos modificados

- `features/documentos/models/documento.model.ts`
- `features/documentos/components/subir-documento-modal/subir-documento-modal.component.html`

## Consideraciones

- `accept` es solo una ayuda del navegador: el Backend es quien rechaza (400) cualquier otra extensión.
- Los prototipos de `vistas/` y los documentos históricos mencionan DICOM; no se tocaron.
- Verificado con `ng build`.
