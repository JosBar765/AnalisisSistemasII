# Corrección 19 — "Nueva contraseña" del formulario de usuarios cambiaba nada (Frontend)

## Problema

Al editar un usuario en `/admin/usuarios`, el campo "Nueva Contraseña (opcional)" se enviaba dentro de
`PUT /usuarios/{id}`, pero el Backend ignora la contraseña en esa operación (`UsuarioMapper.updateEntity` no la
copia; el cambio de contraseña es `PATCH /usuarios/{id}/contrasenia`). El frontend tenía `cambiarContrasenia` en
`UsuarioService` pero ningún componente lo llamaba: la pantalla mostraba éxito y la contraseña seguía siendo la
anterior. Consecuencia: la cuenta editada (p. ej. una secretaria) rechazaba la contraseña "nueva" con
"Correo o contraseña incorrectos."

## Solución

En `usuarios.component.ts`, al **editar**:
- El `PUT` ya no lleva la contraseña.
- Si el campo trae valor, tras guardar los datos se llama a `UsuarioService.cambiarContrasenia(id, valor)`
  (`switchMap`). Si falla (p. ej. más de 72 caracteres) sale el aviso emergente con el mensaje del Backend.
- El campo sigue siendo opcional al editar, pero si se escribe exige mínimo 8 caracteres, igual que al crear.

Al **crear** no cambia nada: la contraseña viaja en el `POST` y el Backend la guarda cifrada.

## Archivos modificados

- `frontend/src/app/features/admin/pages/usuarios/usuarios.component.ts`

## Resultado

`ng build` sin errores. A nivel de API se verificó la secuencia que ahora hace la pantalla: `PUT` sin contraseña
(200) y `PATCH /usuarios/{id}/contrasenia` (200); después el login con la contraseña nueva responde 200 y con la
anterior 401. No se probó haciendo clic en la pantalla.

## Consideraciones

- Un usuario cuya contraseña "cambió" con el formulario antes de esta corrección conserva la **anterior**: hay que
  volver a escribir la nueva en el campo (o llamar al endpoint) para que surta efecto.
- El endpoint recibe la contraseña como parámetro de URL (`?nuevaContrasenia=`), por lo que puede quedar en
  registros de acceso; no se cambió el contrato del Backend.
