import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Paciente, PacienteRequest } from '../models/paciente.model';

@Injectable({ providedIn: 'root' })
export class PacienteService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/pacientes`;

  listar(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.apiUrl);
  }

  registrar(request: PacienteRequest): Observable<Paciente> {
    return this.http.post<Paciente>(this.apiUrl, request);
  }

  modificar(id: number, request: PacienteRequest): Observable<Paciente> {
    return this.http.put<Paciente>(`${this.apiUrl}/${id}`, request);
  }

  cambiarEstado(id: number, estado: boolean): Observable<Paciente> {
    const params = new HttpParams().set('estado', estado);
    return this.http.patch<Paciente>(`${this.apiUrl}/${id}/estado`, null, { params });
  }
}
