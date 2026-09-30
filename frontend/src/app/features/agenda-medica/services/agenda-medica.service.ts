import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, debounceTime } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { WebSocketService } from '../../../core/services/websocket.service';
import { CitaMedica, ConsultaFinalizada } from '../models/agenda-medica.model';

@Injectable({ providedIn: 'root' })
export class AgendaMedicaService {
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

  listarMisCitas(fecha: string): Observable<CitaMedica[]> {
    const params = new HttpParams().set('desde', fecha).set('hasta', fecha);
    return this.http.get<CitaMedica[]>(`${this.apiUrl}/citas/mis-citas`, { params });
  }

  listarMisConsultas(fecha: string): Observable<ConsultaFinalizada[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<ConsultaFinalizada[]>(`${this.apiUrl}/consultas/mias`, { params });
  }

  solicitarLlamado(idCita: number): Observable<CitaMedica> {
    return this.http.patch<CitaMedica>(`${this.apiUrl}/citas/${idCita}/llamado`, null);
  }
}
