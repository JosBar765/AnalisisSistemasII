# Corrección 5 — Archivos huérfanos en Storage y sesiones WebSocket sin autenticar (Backend)

> Resuelve dos pendientes listados en "Consideraciones" de `creacion_modulo_secretaria.md`.

## Problema

1. **Archivo huérfano en Storage.** `DocumentoServiceImpl.subir/actualizar` sube el archivo a
   Supabase Storage *antes* de confirmar la transacción de base de datos. Si después fallaba algo
   (por ejemplo una categoría inexistente o un error al hacer commit), el archivo quedaba en el
   bucket sin ningún `Documento` que lo referenciara.
2. **Sesiones WebSocket sin autenticar.** `CitaWebSocketHandler` exige el JWT como primer mensaje,
   pero una conexión que nunca lo enviaba permanecía abierta indefinidamente (no recibía eventos,
   pero consumía un socket).

## Solución

### 1. Limpieza del archivo nuevo si la transacción no se confirma
- `AlmacenamientoService` tiene un método nuevo `eliminar(String ruta)`, de **mejor esfuerzo**: si
  falla solo registra un `WARN` y no lanza excepción (para no ocultar el error original).
- `DocumentoServiceImpl.guardarArchivo(...)` sube el archivo y registra un
  `TransactionSynchronization.afterCompletion`: si el estado no es `STATUS_COMMITTED`, elimina de
  Storage **solo el archivo recién subido**.
- La regla de negocio no cambia: el archivo **anterior** de un documento reemplazado **nunca** se
  elimina (queda referenciado en `AuditoriaDocumento.url_anterior`).

### 2. Cierre por tiempo de sesiones sin autenticar
- `CitaWebSocketHandler.afterConnectionEstablished` programa una comprobación a los **10 s**
  (`SEGUNDOS_PARA_AUTENTICAR`). Si la sesión sigue abierta y no está en `sesionesAutenticadas`, se
  cierra con `1008 POLICY_VIOLATION` y motivo `Autenticación requerida`.
- El temporizador es un `ScheduledExecutorService` de un hilo demonio, detenido en `@PreDestroy`.
- Un token inválido sigue cerrando de inmediato (`1008`, `Token inválido`).

## Archivos modificados

```
backend/src/main/java/com/josbar/medisistemas/
├── services/AlmacenamientoService.java                     (+ eliminar)
├── services/impl/SupabaseAlmacenamientoServiceImpl.java    (+ eliminar, DELETE /object/{bucket}/{ruta})
├── services/impl/DocumentoServiceImpl.java                 (guardarArchivo con limpieza)
└── config/CitaWebSocketHandler.java                        (temporizador de autenticación)
```

## Resultado (verificado)

Probado contra **Supabase real** y PostgreSQL local, con el backend en ejecución:
- Reemplazo con categoría inexistente (`idCategoriaDocumento=999`): responde 404 y el bucket
  conserva exactamente los 2 archivos previos (no aparece un tercero).
- Reemplazo válido: conserva el archivo anterior y crea la auditoría.
- WebSocket: sin mensaje → cierre a los ~10 s con `1008`; con JWT válido → `AUTH_OK` y la sesión
  sigue abierta; con token falso → `1008` inmediato.
- `mvnw compile` sin errores. Los datos de prueba (paciente, documentos, archivos de Storage) se
  eliminaron al terminar.

## Consideraciones

- Si la eliminación del huérfano falla (Supabase caído en ese instante), queda un `WARN`
  `No se pudo eliminar el archivo huérfano '<ruta>' ...` con la ruta para limpiarlo a mano.
- Si el proceso se cae entre la subida y el final de la transacción, el `afterCompletion` no se
  ejecuta y el archivo sí puede quedar huérfano; no hay barrido periódico del bucket.
- El plazo de 10 s es una constante; el frontend envía el JWT al abrir la conexión, así que no se
  ve afectado.
