# Corrección 4 — Reinicio automático del backend en desarrollo

## Problema
Cada cambio en el código del backend obligaba a detener y volver a lanzar `mvnw spring-boot:run`.

## Solución
Se agregó `spring-boot-devtools` (scope `runtime`, `optional`) al `pom.xml`. Con la aplicación
levantada con `.\mvnw spring-boot:run`, DevTools reinicia el contexto de Spring cuando cambian las
clases compiladas en `target/classes`. No se incluye en el artefacto de producción.

## Flujo de trabajo
1. Terminal 1, desde `backend/`: `.\mvnw spring-boot:run` (se deja abierta).
2. Después de editar código, en la Terminal 2, desde `backend/`: `.\mvnw -q compile`.
   DevTools detecta las clases nuevas y reinicia solo (unos segundos).
3. Los cambios en `application.properties` y en dependencias del `pom.xml` sí requieren
   detener y volver a lanzar el backend.

## Archivos modificados
- `backend/pom.xml`
