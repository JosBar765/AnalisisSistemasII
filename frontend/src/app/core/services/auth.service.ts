import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../models/auth-response.model';
import { LoginRequest } from '../models/login-request.model';
import { Rol, UsuarioAuth } from '../models/usuario-auth.model';

const TOKEN_KEY = 'medisistema_token';

/** Ruta de inicio de cada rol. Se agrega una entrada cuando el módulo del rol exista. */
const RUTA_INICIO: Partial<Record<Rol, string>> = {
  ADMINISTRADOR: '/admin/dashboard',
  SECRETARIA: '/inicio',
};

interface JwtPayload {
  sub: string;
  exp: number;
  rol: Rol;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  private readonly token = signal<string | null>(this.leerTokenVigente());

  readonly usuario = computed<UsuarioAuth | null>(() => {
    const payload = this.decodificar(this.token());
    return payload ? { id: Number(payload.sub), nombre: payload.nombre, rol: payload.rol } : null;
  });

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => {
        localStorage.setItem(TOKEN_KEY, response.token);
        this.token.set(response.token);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.token.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.token();
  }

  estaAutenticado(): boolean {
    const payload = this.decodificar(this.token());
    return !!payload && payload.exp * 1000 > Date.now();
  }

  tieneRol(roles: Rol[]): boolean {
    const usuario = this.usuario();
    return !!usuario && roles.includes(usuario.rol);
  }

  rutaInicio(): string | null {
    const usuario = this.usuario();
    return (usuario && RUTA_INICIO[usuario.rol]) ?? null;
  }

  private leerTokenVigente(): string | null {
    const guardado = localStorage.getItem(TOKEN_KEY);
    const payload = this.decodificar(guardado);
    if (payload && payload.exp * 1000 > Date.now()) {
      return guardado;
    }
    localStorage.removeItem(TOKEN_KEY);
    return null;
  }

  private decodificar(token: string | null): JwtPayload | null {
    if (!token) {
      return null;
    }
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const bytes = Uint8Array.from(atob(base64), (c) => c.charCodeAt(0));
      return JSON.parse(new TextDecoder().decode(bytes)) as JwtPayload;
    } catch {
      return null;
    }
  }
}
