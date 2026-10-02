# Corrección 17 — El formato DICOM (.dcm) deja de ser un formato permitido (Backend)

## Problema

La subida de documentos permitía `pdf, jpg, jpeg, png` y `dcm` (DICOM, imágenes médicas). El bucket de Supabase no
acepta ese tipo de archivo, por lo que subirlo fallaba después de pasar la validación.

## Solución

- `DocumentoServiceImpl.EXTENSIONES_PERMITIDAS` ahora es `pdf, jpg, jpeg, png`.
- El mensaje de rechazo dice: "Tipo de archivo no permitido. Formatos válidos: PDF, JPG, JPEG y PNG." (400, antes de
  subir nada a Storage).

## Archivos modificados

- `backend/src/main/java/com/josbar/medisistemas/services/impl/DocumentoServiceImpl.java`

## Resultado (verificado)

`POST /documentos` con un `.dcm` → 400 con el mensaje anterior.

## Consideraciones

- Los documentos `.dcm` que ya estuvieran guardados siguen existiendo y se pueden ver; solo no se pueden subir ni
  usar como reemplazo.
- La validación es por **extensión del nombre**, no por el contenido del archivo.
- Esto reemplaza lo indicado en `creacion_modulo_secretaria.md` ("Formatos permitidos: pdf, jpg, jpeg, png, dcm").
  Parte frontend: `frontend/documentacion/correcciones13.md`.
