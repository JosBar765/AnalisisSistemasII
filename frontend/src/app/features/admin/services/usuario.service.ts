import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Usuario, UsuarioRequest } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/usuarios`;

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }

  registrar(request: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, request);
  }

  modificar(id: number, request: UsuarioRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, request);
  }

  cambiarEstado(id: number, estado: boolean): Observable<Usuario> {
    const params = new HttpParams().set('estado', estado);
    return this.http.patch<Usuario>(`${this.apiUrl}/${id}/estado`, null, { params });
  }

  cambiarContrasenia(id: number, nuevaContrasenia: string): Observable<void> {
    const params = new HttpParams().set('nuevaContrasenia', nuevaContrasenia);
    return this.http.patch<void>(`${this.apiUrl}/${id}/contrasenia`, null, { params });
  }
}
