import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../models/auth-response.model';
import { LoginRequest } from '../models/login-request.model';

/**
 * Nota: por ahora el login del Frontend no invoca a este servicio (ver
 * layouts/login/login.component.ts) — redirige directo al dashboard sin
 * validar credenciales, según lo solicitado para esta fase. El método login()
 * queda disponible para cuando se conecte la autenticación real basada en JWT.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }
}
