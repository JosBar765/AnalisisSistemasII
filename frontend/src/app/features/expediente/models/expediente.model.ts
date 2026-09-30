import { Persona } from '../../../shared/pipes/nombre-completo.pipe';

export interface PacienteExpediente extends Persona {
  id: number;
  dpi: string;
  fechaNacimiento: string;
  telefono: string;
  correo: string;
  direccion: string;
  estado: boolean;
}

export interface SignosVitalesExpediente {
  peso: number;
  altura: number;
  presionSistolica: number;
  presionDiastolica: number;
  temperatura: number;
}

export interface ConsultaExpediente {
  id: number;
  fecha: string;
  hora: string;
  idMedico: number;
  nombreMedico: string;
  motivoConsulta: string;
  diagnostico: string;
  tratamiento: string;
  observaciones: string | null;
  signosVitalesRequestDTO: SignosVitalesExpediente | null;
}

export interface DocumentoExpediente {
  id: number;
  categoriaDocumentoResponseDTO: { id: number; nombre: string };
  nombre: string;
  fechaCarga: string;
}

export interface Expediente {
  pacienteResponseDTO: PacienteExpediente;
  historialConsultasResponseDTO: ConsultaExpediente[];
  documentosClinicosResponseDTO: DocumentoExpediente[];
}
