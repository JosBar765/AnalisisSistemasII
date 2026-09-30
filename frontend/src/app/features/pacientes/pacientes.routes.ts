import { Routes } from '@angular/router';

export const PACIENTES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/listar/listar.component').then((m) => m.ListarPacientesComponent),
  },
];
