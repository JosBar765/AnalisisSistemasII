import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Catalogo } from '../models/catalogo.model';

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/catalogos`;

  listarRoles(): Observable<Catalogo[]> {
    return this.http.get<Catalogo[]>(`${this.apiUrl}/roles`);
  }

  listarEspecialidades(): Observable<Catalogo[]> {
    return this.http.get<Catalogo[]>(`${this.apiUrl}/especialidades`);
  }

  listarDiasSemana(): Observable<Catalogo[]> {
    return this.http.get<Catalogo[]>(`${this.apiUrl}/dias-semana`);
  }
}
