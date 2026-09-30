import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Expediente, PacienteExpediente } from '../models/expediente.model';

@Injectable({ providedIn: 'root' })
export class ExpedienteService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  listarPacientes(): Observable<PacienteExpediente[]> {
    return this.http.get<PacienteExpediente[]>(`${this.apiUrl}/pacientes`);
  }

  obtener(idPaciente: number): Observable<Expediente> {
    return this.http.get<Expediente>(`${this.apiUrl}/expedientes/paciente/${idPaciente}`);
  }

  obtenerEnlaceDocumento(idDocumento: number): Observable<{ url: string }> {
    return this.http.get<{ url: string }>(`${this.apiUrl}/documentos/${idDocumento}/enlace`);
  }
}
