import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    loadComponent: () => import('./layouts/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMINISTRADOR'] },
    loadComponent: () => import('./layouts/main/main-layout.component').then((m) => m.MainLayoutComponent),
    loadChildren: () => import('./features/admin/admin.routes').then((m) => m.ADMIN_ROUTES),
  },
  {
    // Módulo de Secretaria: el llamador es una pantalla completa, fuera del layout principal.
    path: '',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['SECRETARIA'] },
    children: [
      {
        path: '',
        loadComponent: () => import('./layouts/main/main-layout.component').then((m) => m.MainLayoutComponent),
        children: [
          {
            path: 'inicio',
            loadComponent: () =>
              import('./features/citas/pages/inicio/inicio.component').then((m) => m.InicioComponent),
          },
          {
            path: 'pacientes',
            loadChildren: () => import('./features/pacientes/pacientes.routes').then((m) => m.PACIENTES_ROUTES),
          },
          {
            path: 'citas',
            loadChildren: () => import('./features/citas/citas.routes').then((m) => m.CITAS_ROUTES),
          },
          {
            path: 'documentos',
            loadChildren: () => import('./features/documentos/documentos.routes').then((m) => m.DOCUMENTOS_ROUTES),
          },
        ],
      },
      {
        path: 'llamador',
        loadComponent: () =>
          import('./features/citas/pages/llamador/llamador.component').then((m) => m.LlamadorComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
