import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'usuarios',
    loadComponent: () => import('./pages/usuarios/usuarios.component').then((m) => m.UsuariosComponent),
  },
  {
    path: 'especialidades',
    loadComponent: () =>
      import('./pages/especialidades/especialidades.component').then((m) => m.EspecialidadesComponent),
  },
  {
    path: 'medicos',
    loadComponent: () => import('./pages/medicos/medicos.component').then((m) => m.MedicosComponent),
  },
  {
    path: 'jornadas',
    loadComponent: () => import('./pages/jornadas/jornadas.component').then((m) => m.JornadasComponent),
  },
];
