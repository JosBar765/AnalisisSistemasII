import { Routes } from '@angular/router';

/**
 * Rutas del feature de citas. La agenda cuelga de `/citas`; el inicio de recepción y el llamador
 * se montan en `app.route.ts` porque el llamador es una pantalla completa fuera del layout principal.
 */
export const CITAS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/agenda/agenda.component').then((m) => m.AgendaComponent),
  },
];
