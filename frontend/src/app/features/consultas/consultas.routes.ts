import { Routes } from '@angular/router';

/** `/consultas/nueva/:idCita` atiende una cita; `/consultas/:id` muestra una consulta y su auditoría. */
export const CONSULTAS_ROUTES: Routes = [
  {
    path: 'nueva/:idCita',
    loadComponent: () => import('./pages/nueva/nueva.component').then((m) => m.NuevaConsultaComponent),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/detalle/detalle.component').then((m) => m.DetalleConsultaComponent),
  },
];
