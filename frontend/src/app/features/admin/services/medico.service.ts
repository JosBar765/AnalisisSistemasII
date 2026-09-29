import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Medico, MedicoRequest } from '../models/medico.model';

@Injectable({ providedIn: 'root' })
export class MedicoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/medicos`;

  listar(): Observable<Medico[]> {
    return this.http.get<Medico[]>(this.apiUrl);
  }

  registrar(request: MedicoRequest): Observable<Medico> {
    return this.http.post<Medico>(this.apiUrl, request);
  }

  modificar(id: number, request: Partial<MedicoRequest>): Observable<Medico> {
    return this.http.put<Medico>(`${this.apiUrl}/${id}`, request);
  }

  /**
   * El médico comparte id con su Usuario (ver MediSistema.sql), por lo que
   * activar/inactivar un médico se resuelve reutilizando el endpoint de
   * estado de Usuario en vez de duplicar la funcionalidad en /medicos.
   */
  cambiarEstado(idMedico: number, estado: boolean): Observable<void> {
    const params = new HttpParams().set('estado', estado);
    return this.http.patch<void>(`${environment.apiUrl}/usuarios/${idMedico}/estado`, null, { params });
  }
}
