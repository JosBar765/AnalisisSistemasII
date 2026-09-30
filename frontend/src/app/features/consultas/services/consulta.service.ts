import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  AuditoriaConsulta,
  CatalogoConsulta,
  CitaConsulta,
  Consulta,
  ModificarConsultaRequest,
  RegistrarConsultaRequest,
} from '../models/consulta.model';

@Injectable({ providedIn: 'root' })
export class ConsultaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  obtenerCita(idCita: number): Observable<CitaConsulta> {
    return this.http.get<CitaConsulta>(`${this.apiUrl}/citas/mis-citas/${idCita}`);
  }

  registrar(request: RegistrarConsultaRequest): Observable<Consulta> {
    return this.http.post<Consulta>(`${this.apiUrl}/consultas`, request);
  }

  obtener(id: number): Observable<Consulta> {
    return this.http.get<Consulta>(`${this.apiUrl}/consultas/${id}`);
  }

  modificar(id: number, request: ModificarConsultaRequest): Observable<Consulta> {
    return this.http.put<Consulta>(`${this.apiUrl}/consultas/${id}`, request);
  }

  listarAuditoria(id: number): Observable<AuditoriaConsulta[]> {
    return this.http.get<AuditoriaConsulta[]>(`${this.apiUrl}/auditorias/consultas/${id}`);
  }

  listarMotivosModificacion(): Observable<CatalogoConsulta[]> {
    return this.http.get<CatalogoConsulta[]>(`${this.apiUrl}/catalogos/motivos-consulta`);
  }
}
