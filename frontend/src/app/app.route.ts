import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    loadComponent: () => import('./layouts/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'admin',
    loadComponent: () => import('./layouts/main/main-layout.component').then((m) => m.MainLayoutComponent),
    loadChildren: () => import('./features/admin/admin.routes').then((m) => m.ADMIN_ROUTES),
  },
  { path: '**', redirectTo: 'login' },
];
