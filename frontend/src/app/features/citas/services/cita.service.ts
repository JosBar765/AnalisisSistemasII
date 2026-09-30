import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, debounceTime } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { WebSocketService } from '../../../core/services/websocket.service';
import { Cita, CitaRequest, HorarioDisponible, MedicoCita, PacienteCita } from '../models/cita.model';

@Injectable({ providedIn: 'root' })
export class CitaService {
  private readonly http = inject(HttpClient);
  private readonly webSocket = inject(WebSocketService);
  private readonly apiUrl = environment.apiUrl;

  /** Avisa que las citas cambiaron (o que se reconectó el canal): conviene volver a consultar. */
  readonly cambios$: Observable<unknown> = this.webSocket.eventos$.pipe(debounceTime(250));

  hoy(): string {
    const ahora = new Date();
    const mes = String(ahora.getMonth() + 1).padStart(2, '0');
    const dia = String(ahora.getDate()).padStart(2, '0');
    return `${ahora.getFullYear()}-${mes}-${dia}`;
  }

  listarAgenda(fecha: string): Observable<Cita[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<Cita[]>(`${this.apiUrl}/citas/agenda-diaria`, { params });
  }

  listarAgendaRango(desde: string, hasta: string): Observable<Cita[]> {
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<Cita[]>(`${this.apiUrl}/citas/agenda`, { params });
  }

  listarHorarios(idMedico: number, fecha: string): Observable<HorarioDisponible[]> {
    const params = new HttpParams().set('idMedico', idMedico).set('fecha', fecha);
    return this.http.get<HorarioDisponible[]>(`${this.apiUrl}/citas/disponibilidad`, { params });
  }

  programar(request: CitaRequest): Observable<Cita> {
    return this.http.post<Cita>(`${this.apiUrl}/citas`, request);
  }

  reprogramar(id: number, request: CitaRequest): Observable<Cita> {
    return this.http.put<Cita>(`${this.apiUrl}/citas/${id}/reprogramar`, request);
  }

  cancelar(id: number): Observable<Cita> {
    return this.http.put<Cita>(`${this.apiUrl}/citas/${id}/cancelar`, null);
  }

  registrarLlegada(id: number): Observable<Cita> {
    return this.http.patch<Cita>(`${this.apiUrl}/citas/${id}/llegada`, null);
  }

  listarMedicos(): Observable<MedicoCita[]> {
    return this.http.get<MedicoCita[]>(`${this.apiUrl}/medicos`);
  }

  listarPacientes(): Observable<PacienteCita[]> {
    return this.http.get<PacienteCita[]>(`${this.apiUrl}/pacientes`);
  }
}
