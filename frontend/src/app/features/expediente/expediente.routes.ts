import { Routes } from '@angular/router';

export const EXPEDIENTE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/buscar/buscar.component').then((m) => m.BuscarExpedienteComponent),
  },
  {
    path: ':idPaciente',
    loadComponent: () => import('./pages/detalle/detalle.component').then((m) => m.DetalleExpedienteComponent),
  },
];
