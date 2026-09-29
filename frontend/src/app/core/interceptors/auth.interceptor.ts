import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const esApi = req.url.startsWith(environment.apiUrl);
  const token = auth.getToken();

  const solicitud =
    esApi && token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(solicitud).pipe(
    catchError((error: HttpErrorResponse) => {
      // Token vencido o inválido: se cierra la sesión (el login mismo responde 401 por credenciales).
      if (error.status === 401 && token && !req.url.endsWith('/auth/login')) {
        auth.logout();
      }
      return throwError(() => error);
    }),
  );
};
