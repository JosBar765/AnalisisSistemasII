import { Injectable, effect, inject } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Rol } from '../models/usuario-auth.model';
import { AuthService } from './auth.service';

export type TipoEventoTiempoReal =
  | 'CITA_CREADA'
  | 'CITA_ACTUALIZADA'
  | 'CITA_CANCELADA'
  | 'CITA_ATENDIDA'
  | 'CONECTADO';

export interface EventoTiempoReal {
  tipo: TipoEventoTiempoReal;
  citaId?: number;
  medicoId?: number;
}

/** Roles que reciben eventos en tiempo real. El Backend decide qué evento recibe cada uno. */
const ROLES_CON_TIEMPO_REAL: Rol[] = ['SECRETARIA', 'MEDICO'];
const MS_REINTENTO = 3000;

/**
 * Conexión WebSocket única de la aplicación. Se abre mientras haya una sesión válida de un rol que
 * la necesite y se cierra al cerrar sesión. Autentica enviando el JWT como primer mensaje (el token
 * no viaja en la URL). Emite `CONECTADO` cada vez que la conexión queda autenticada, para que los
 * features vuelvan a consultar lo que pudieron perderse mientras estuvo caída.
 */
@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private readonly auth = inject(AuthService);
  private readonly eventos = new Subject<EventoTiempoReal>();

  readonly eventos$: Observable<EventoTiempoReal> = this.eventos.asObservable();

  private socket: WebSocket | null = null;
  private reintento: ReturnType<typeof setTimeout> | null = null;
  private cierreIntencional = false;

  constructor() {
    effect(() => {
      const usuario = this.auth.usuario();
      if (usuario && ROLES_CON_TIEMPO_REAL.includes(usuario.rol)) {
        this.conectar();
      } else {
        this.desconectar();
      }
    });
  }

  private conectar(): void {
    const token = this.auth.getToken();
    if (this.socket || !token) {
      return;
    }
    this.cierreIntencional = false;

    const socket = new WebSocket(environment.apiUrl);
    this.socket = socket;

    socket.onopen = () => socket.send(token);
    socket.onmessage = (mensaje) => this.procesar(mensaje.data);
    socket.onclose = () => {
      this.socket = null;
      if (!this.cierreIntencional && this.auth.estaAutenticado()) {
        this.reintento = setTimeout(() => this.conectar(), MS_REINTENTO);
      }
    };
  }

  private desconectar(): void {
    this.cierreIntencional = true;
    if (this.reintento) {
      clearTimeout(this.reintento);
      this.reintento = null;
    }
    this.socket?.close();
    this.socket = null;
  }

  private procesar(datos: string): void {
    try {
      const evento = JSON.parse(datos) as { tipo: string; citaId?: number; medicoId?: number };
      if (evento.tipo === 'AUTH_OK') {
        this.eventos.next({ tipo: 'CONECTADO' });
      } else {
        this.eventos.next(evento as EventoTiempoReal);
      }
    } catch {
      // Mensaje que no es JSON: se ignora.
    }
  }
}
