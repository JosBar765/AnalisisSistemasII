import { Persona } from '../../../shared/pipes/nombre-completo.pipe';

export interface CatalogoDocumento {
  id: number;
  nombre: string;
}

export interface PacienteDocumento extends Persona {
  id: number;
  dpi: string;
  estado: boolean;
}

export interface Documento {
  id: number;
  idPaciente: number;
  categoriaDocumentoResponseDTO: CatalogoDocumento;
  nombre: string;
  url: string;
  fechaCarga: string;
  idUsuarioCarga: number;
}

export interface AuditoriaDocumento {
  id: number;
  idDocumento: number;
  nombreAnterior: string;
  urlAnterior: string | null;
  nombreUsuario: string;
  fechaModificacion: string;
  motivoModificacion: string;
  nombreNuevo: string;
  urlNuevo: string | null;
}

export const EXTENSIONES_PERMITIDAS = '.pdf,.jpg,.jpeg,.png,.dcm';
