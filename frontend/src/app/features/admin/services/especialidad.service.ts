import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Catalogo } from '../models/catalogo.model';
import { CatalogoService } from './catalogo.service';

@Injectable({ providedIn: 'root' })
export class EspecialidadService {
  private readonly http = inject(HttpClient);
  private readonly catalogoService = inject(CatalogoService);
  private readonly apiUrl = `${environment.apiUrl}/especialidades`;

  listar(): Observable<Catalogo[]> {
    return this.catalogoService.listarEspecialidades();
  }

  registrar(nombre: string): Observable<Catalogo> {
    return this.http.post<Catalogo>(this.apiUrl, { nombre });
  }

  editar(id: number, nombre: string): Observable<Catalogo> {
    return this.http.put<Catalogo>(`${this.apiUrl}/${id}`, { nombre });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
