import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { JornadaMedica, JornadaMedicaRequest } from '../models/jornada.model';

@Injectable({ providedIn: 'root' })
export class JornadaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/jornadas`;

  listarPorMedico(idMedico: number): Observable<JornadaMedica[]> {
    return this.http.get<JornadaMedica[]>(`${this.apiUrl}/medico/${idMedico}`);
  }

  registrar(request: JornadaMedicaRequest): Observable<JornadaMedica> {
    return this.http.post<JornadaMedica>(this.apiUrl, request);
  }

  modificar(id: number, request: Partial<JornadaMedicaRequest>): Observable<JornadaMedica> {
    return this.http.put<JornadaMedica>(`${this.apiUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
