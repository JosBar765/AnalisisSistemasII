import { Persona } from '../../../shared/pipes/nombre-completo.pipe';

export interface CatalogoCita {
  id: number;
  nombre: string;
}

export interface MedicoCita {
  id: number;
  usuarioResponseDTO: Persona & { estado: boolean };
  especialidadResponseDTO: CatalogoCita;
  colegiado: string;
}

export interface PacienteCita extends Persona {
  id: number;
  dpi: string;
  estado: boolean;
}

export interface Cita {
  id: number;
  medicoResponseDTO: MedicoCita;
  pacienteResponseDTO: PacienteCita;
  estadoCitaResponseDTO: CatalogoCita;
  fecha: string;
  hora: string;
  horaLlegada: string | null;
  horaSolicitudLlamado: string | null;
}

export interface CitaRequest {
  idMedico: number;
  idPaciente: number;
  fecha: string;
  hora: string;
}

export interface HorarioDisponible {
  hora: string;
}

export const ESTADO_EN_ESPERA = 'En espera';
export const ESTADO_ATENDIDO = 'Atendido';
export const ESTADO_CANCELADO = 'Cancelado';

export function estaEnEspera(cita: Cita): boolean {
  return cita.estadoCitaResponseDTO.nombre === ESTADO_EN_ESPERA;
}

export function claseEstado(cita: Cita): string {
  switch (cita.estadoCitaResponseDTO.nombre) {
    case ESTADO_ATENDIDO:
      return 'badge-success';
    case ESTADO_CANCELADO:
      return 'badge-danger';
    default:
      return 'badge-warning';
  }
}

/** Pacientes presentes que esperan ser atendidos, en el orden en que llegaron. */
export function presentesEnEspera(citas: Cita[]): Cita[] {
  return citas
    .filter((c) => estaEnEspera(c) && !!c.horaLlegada)
    .sort((a, b) => a.horaLlegada!.localeCompare(b.horaLlegada!));
}

/** Llamados que el médico solicitó y la secretaria aún debe hacer; el más reciente primero. */
export function llamadosPendientes(citas: Cita[]): Cita[] {
  return citas
    .filter((c) => estaEnEspera(c) && !!c.horaSolicitudLlamado)
    .sort((a, b) => b.horaSolicitudLlamado!.localeCompare(a.horaSolicitudLlamado!));
}
