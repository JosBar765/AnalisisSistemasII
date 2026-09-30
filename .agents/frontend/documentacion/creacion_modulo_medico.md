# Creación — Módulo Médico (Frontend)

## Objetivo

Implementar en Angular las pantallas del Médico propuestas en `vistas/medico/`, respetando
`reglas_estructura_y_features.md`: un feature por funcionalidad (`agenda-medica`, `consultas`,
`expediente`), sin importaciones entre features, conexión WebSocket única en `core` y acceso por rol.
Consume la API descrita en `backend/documentacion/creacion_modulo_medico.md`.

## Archivos

```
frontend/src/app/
├── shared/pipes/edad.pipe.ts                      (edad en años; usado por los 3 features)
├── features/
│   ├── agenda-medica/   models/, services/agenda-medica.service.ts, pages/agenda/
│   ├── consultas/       consultas.routes.ts, models/, services/consulta.service.ts,
│   │                    components/{signos-vitales-campos, modificar-consulta-modal}/,
│   │                    pages/{nueva, detalle}/
│   └── expediente/      expediente.routes.ts, models/, services/expediente.service.ts,
│                        pages/{buscar, detalle}/
```
Modificados: `app.route.ts`, `core/services/auth.service.ts`, `layouts/main/main-layout.component.*` y
`src/styles.css`.

## Rutas (todas bajo `authGuard` + `roleGuard`, rol `MEDICO`, layout principal)

| Ruta | Pantalla |
| --- | --- |
| `/agenda` (inicio del rol) | Mi Agenda y Cola |
| `/expedientes` | Buscador de pacientes |
| `/expedientes/:idPaciente` | Expediente clínico |
| `/consultas/nueva/:idCita` | Atención clínica (consulta activa) |
| `/consultas/:id` | Consulta finalizada + bitácora de auditoría + modificar |

`AuthService.RUTA_INICIO` ahora incluye `MEDICO: '/agenda'`. El menú lateral del layout se decide por rol
(`etiquetaPanel`, `etiquetaRol`, `esMedico`): Mi Agenda y Cola, Expedientes Clínicos. "Consulta Activa" y
"Auditoría de Consultas" del mockup no son entradas de menú: son pantallas contextuales que se abren con una
cita o una consulta concretas.

## Funcionalidades

- **Agenda** (UC-MED-001): selector de fecha (hoy por defecto). Para hoy muestra la **cola** de pacientes
  presentes (en espera con llegada registrada) por orden de llegada, el botón *Solicitar llamar siguiente
  paciente* (pide el llamado del primero sin solicitud), por fila *Solicitar llamado* y, una vez solicitado,
  *Atender consulta*; además un banner por cada llamado enviado. Siempre muestra las citas de la fecha con su
  estado y las **consultas finalizadas** (enlace a `/consultas/:id`). Se actualiza en vivo por WebSocket
  (`AgendaMedicaService.cambios$`, con `debounceTime`, igual que `CitaService` de Secretaria).
- **Consulta activa** (UC-MED-003): ficha del paciente y formulario reactivo (motivo, signos vitales,
  diagnóstico, tratamiento, observaciones). Al guardar vuelve a `/agenda`. Muestra el mensaje de negocio del
  Backend si la cita ya no está "En espera" o ya tiene consulta.
- **Consulta finalizada** (UC-MED-004): datos actuales y **bitácora** (más reciente primero) con comparación
  *anterior / nueva* de **solo los campos que cambiaron** (`cambiosDeAuditoria` en `consulta.model.ts`).
  *Editar consulta* (modal prellenado, motivo de modificación obligatorio del catálogo) solo aparece si
  `consulta.idMedico === usuario.id`; el Backend lo exige igualmente.
- **Expediente** (UC-MED-002): datos del paciente, últimos signos vitales (de la consulta más reciente que
  los tenga), historial de consultas (enlace a la consulta) y documentos con *Ver* (nueva pestaña con
  enlace firmado) y *Descargar* (mismo enlace con `&download=<nombre>`, que Supabase responde con
  `Content-Disposition: attachment`). Se llega desde el buscador o desde la agenda/consulta.

## Integración

```
Page → Feature Service (HttpClient) → /citas/mis-citas, /consultas, /expedientes, /documentos, /auditorias
Page → AgendaMedicaService.cambios$ → WebSocketService (core) → /ws/citas   (evento → nueva consulta REST)
```
Sinergia con Secretaria: la cola del médico se alimenta de la *llegada* que registra la secretaria; el
*llamado* del médico aparece en el Inicio y el Llamador de la secretaria; al finalizar la consulta la cita
queda "Atendido" y ambos lo ven en vivo (`CITA_ATENDIDA`). La secretaria no tiene acceso a las pantallas ni
a los endpoints clínicos.

## Diferencias con las vistas propuestas (por el modelo de datos o el análisis)

- Sin "No. Expediente", sexo, "Procedencia" del documento ni "Estado en consulta" (no existen en el modelo).
- La altura se captura en **cm** (así lo define `MediSistema.sql`); el mockup la pedía en metros.
- El diagnóstico es texto libre (no hay catálogo CIE-10).
- El motivo de modificación es un **catálogo**, no texto libre (así es el modelo), como en documentos.
- La cola se calcula con la llegada y el llamado, no con un estado "En consulta" (los estados son tres).

## Consideraciones

- Los features no se importan entre sí: `agenda-medica` replica dos funciones mínimas de `citas`
  (`estaEnEspera`, `presentesEnEspera`) con su propio modelo, en vez de acoplarse al feature de Secretaria.
  Los enlaces entre pantallas son URL, no importaciones.
- `SignosVitalesCamposComponent` (y `crearGrupoSignosVitales`) se comparte entre la consulta nueva y el
  modal de modificación dentro del feature `consultas`.
- Estilos nuevos globales en `styles.css`: `.patient-summary-header`, `.vitals-grid`, `.vital-box`,
  `.diff-grid`, `.diff-box-old/new` (los mockups usaban clases que nunca existieron en `main.css`).
- Verificado con `ng build` y **en navegador** contra el Backend real: login del médico → `/agenda`, solicitar
  llamado, atender, guardar consulta (cita pasa a "Atendido"), modificar con motivo y ver la bitácora, y
  expediente con historial y documentos. Datos de prueba eliminados después.
- No se probó *Ver Documento* (abre ventana nueva) desde el navegador; el enlace firmado y `&download=` se
  verificaron por HTTP contra Supabase.
- El nombre del médico en el menú sale del JWT (`nombre`); su especialidad no se muestra (ver Backend).
