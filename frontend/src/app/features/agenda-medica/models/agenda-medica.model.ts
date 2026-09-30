import { Persona } from '../../../shared/pipes/nombre-completo.pipe';

export interface CatalogoAgenda {
  id: number;
  nombre: string;
}

export interface PacienteAgenda extends Persona {
  id: number;
  dpi: string;
  fechaNacimiento: string;
}

export interface CitaMedica {
  id: number;
  pacienteResponseDTO: PacienteAgenda;
  estadoCitaResponseDTO: CatalogoAgenda;
  fecha: string;
  hora: string;
  horaLlegada: string | null;
  horaSolicitudLlamado: string | null;
}

export interface ConsultaFinalizada {
  id: number;
  idCita: number;
  hora: string;
  idPaciente: number;
  nombrePaciente: string;
  diagnostico: string;
}

export const ESTADO_EN_ESPERA = 'En espera';
export const ESTADO_ATENDIDO = 'Atendido';
export const ESTADO_CANCELADO = 'Cancelado';

export function estaEnEspera(cita: CitaMedica): boolean {
  return cita.estadoCitaResponseDTO.nombre === ESTADO_EN_ESPERA;
}

/** Pacientes presentes que esperan ser atendidos, en el orden en que llegaron. */
export function presentesEnEspera(citas: CitaMedica[]): CitaMedica[] {
  return citas
    .filter((c) => estaEnEspera(c) && !!c.horaLlegada)
    .sort((a, b) => a.horaLlegada!.localeCompare(b.horaLlegada!));
}

export function claseEstado(cita: CitaMedica): string {
  switch (cita.estadoCitaResponseDTO.nombre) {
    case ESTADO_ATENDIDO:
      return 'badge-success';
    case ESTADO_CANCELADO:
      return 'badge-danger';
    default:
      return cita.horaLlegada ? 'badge-info' : 'badge-warning';
  }
}
