import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Rol } from '../models/usuario-auth.model';
import { AuthService } from '../services/auth.service';

/** Uso: `canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] }` */
export const roleGuard: CanActivateFn = (route) => {
  const roles = (route.data['roles'] ?? []) as Rol[];
  return inject(AuthService).tieneRol(roles) ? true : inject(Router).createUrlTree(['/login']);
};
