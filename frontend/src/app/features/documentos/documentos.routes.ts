import { Routes } from '@angular/router';

export const DOCUMENTOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/listar/listar.component').then((m) => m.ListarDocumentosComponent),
  },
];
