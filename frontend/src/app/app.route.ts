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
  { path: '**', redirectTo: 'login' },
];
