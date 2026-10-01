# Corrección 9 — "Segundo apellido" obligatorio en el formulario de usuarios (Frontend)

## Problema

`Usuario.segundo_apellido` es `NOT NULL` en la base de datos y `UsuarioServiceImpl.save` lo exige, pero el formulario
de `/admin/usuarios` lo trataba como opcional. Al dejarlo vacío el usuario veía el aviso de error del servidor
("El campo «segundo apellido» es obligatorio") en lugar de que la pantalla se lo indicara antes de enviar.

## Solución

Criterio aplicado: **si la base de datos exige un campo, la pantalla también**.

- `usuarios.component.ts`: `segundoApellido: ['', Validators.required]` (el botón "Guardar Usuario" queda
  deshabilitado mientras falte).
- `usuarios.component.html`: el input lleva el atributo `required`, igual que primer nombre y primer apellido.

Se revisaron los demás formularios contra las columnas `NOT NULL` y no tienen otra diferencia: pacientes (todos los
campos obligatorios ya lo eran), médicos, jornadas, especialidades, citas y consulta. El segundo nombre sigue siendo
opcional porque la columna admite `NULL`.

## Archivos modificados

- `frontend/src/app/features/admin/pages/usuarios/usuarios.component.ts`
- `frontend/src/app/features/admin/pages/usuarios/usuarios.component.html`

## Consideraciones

- Un usuario ya guardado con el segundo apellido vacío no puede existir (la columna es `NOT NULL`), así que la
  edición no se ve afectada.
- Pendiente conocido (no se corrige por decisión del usuario): en la edición de un usuario, "Nueva contraseña
  (opcional)" no cambia la contraseña; `PUT /usuarios/{id}` la ignora y la pantalla no llama a
  `PATCH /usuarios/{id}/contrasenia`.
- Verificado con `ng build`.
