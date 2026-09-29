# Corrección 1 — LazyInitializationException en endpoints GET (500)

## Problema

Peticiones GET como `/usuarios` y `/medicos` devolvían 500 con
`LazyInitializationException` al serializar el DTO. Ocurría en cualquier
entidad con una relación `@ManyToOne`/`@OneToOne(fetch = FetchType.LAZY)` que
el *Mapper (`UsuarioMapper`, `MedicoMapper`, etc.) intenta leer, por ejemplo
`UsuarioEntity.rolEntity` o `MedicoEntity.especialidad`.

## Causa

En `application.properties` se había fijado `spring.jpa.open-in-view=false`.
Con esa opción, la sesión de Hibernate se cierra en cuanto retorna el método
del Service. El problema es que en este proyecto la conversión Entity → DTO
ocurre en el **Controller**, no dentro del Service:

```java
// UsuarioController
List<UsuarioResponseDTO> usuarios = usuarioService.findAll().stream()
        .map(usuarioMapper::toResponse)   // <- aquí se lee rolEntity.getRol()
        .collect(Collectors.toList());
```

Cuando `usuarioService.findAll()` retorna, el proxy `@Transactional` (si lo
hubiera) ya hizo commit y cerró el `EntityManager` — el `.map(...)` posterior,
en el Controller, se ejecuta con la sesión ya cerrada. Por eso agregar
`@Transactional(readOnly = true)` únicamente en el Service **no resuelve el
problema mientras el mapeo a DTO siga en el Controller**: la solución sirve
solo si el mapeo se mueve adentro del propio método `@Transactional`.

## Solución aplicada

Se revirtió `spring.jpa.open-in-view` a `true` (el valor por defecto de Spring
Boot, que había sido deshabilitado sin necesidad). Con OSIV activo, la sesión
de Hibernate permanece abierta durante toda la request HTTP, así que el
Controller puede seguir mapeando entidades a DTO sin tocar la arquitectura
Controller → Service → Mapper ya establecida en todo el proyecto.

Se descartó deliberadamente:

- **Cambiar las relaciones a `FetchType.EAGER`**: "funciona" pero se
  propaga — cada entidad que referencia a otra con una relación LAZY
  necesitaría el mismo cambio (`Usuario→Rol`, `Medico→Usuario→Rol`,
  `Medico→Especialidad`, `JornadaMedica→Medico→...`, `Cita→Medico/Paciente/
  EstadoCita`, etc.), y además carga relaciones completas aunque no se
  necesiten, perdiendo el beneficio de LAZY.
- **Mover el mapeo Entity → DTO al Service con `@Transactional(readOnly =
  true)`**: es la alternativa "más correcta" a largo plazo (evita que las
  entidades JPA salgan de la capa de Service), pero implica modificar todos
  los Controllers y Services del proyecto — más allá del mínimo cambio
  necesario para corregir el error reportado.

## Archivos modificados

- `backend/src/main/resources/application.properties`

## Resultado

Los endpoints GET que serializan relaciones LAZY (`/usuarios`, `/medicos`,
`/jornadas/medico/{id}`, `/dashboard`, etc.) ya no deberían lanzar
`LazyInitializationException`. Las entidades y sus mappers no se modificaron.

## Consideración para el futuro

Si el proyecto crece y se quiere evitar Open Session In View (por ejemplo,
para prevenir problemas de N+1 al serializar objetos grandes), la forma
correcta de hacerlo sin volver a `FetchType.EAGER` es mover la conversión a
DTO dentro de los métodos `@Transactional(readOnly = true)` de cada Service,
para que los Controllers reciban DTOs listos y nunca entidades JPA.
