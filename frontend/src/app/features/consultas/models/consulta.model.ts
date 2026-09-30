import { Persona } from '../../../shared/pipes/nombre-completo.pipe';

export interface CatalogoConsulta {
  id: number;
  nombre: string;
}

export interface SignosVitales {
  peso: number;
  altura: number;
  presionSistolica: number;
  presionDiastolica: number;
  temperatura: number;
}

export interface CitaConsulta {
  id: number;
  pacienteResponseDTO: Persona & { id: number; dpi: string; fechaNacimiento: string };
  estadoCitaResponseDTO: CatalogoConsulta;
  fecha: string;
  hora: string;
}

export interface Consulta {
  id: number;
  idCita: number;
  fecha: string;
  hora: string;
  idMedico: number;
  nombreMedico: string;
  idPaciente: number;
  nombrePaciente: string;
  motivoConsulta: string;
  diagnostico: string;
  tratamiento: string;
  observaciones: string | null;
  signosVitalesRequestDTO: SignosVitales | null;
}

export interface RegistrarConsultaRequest {
  idCita: number;
  motivoConsulta: string;
  diagnostico: string;
  tratamiento: string;
  observaciones?: string;
  signosVitalesRequestDTO: SignosVitales;
}

export interface ModificarConsultaRequest {
  idMotivoModificacionConsulta: number;
  motivoConsulta: string;
  diagnostico: string;
  tratamiento: string;
  observaciones: string;
  signosVitalesRequestDTO: SignosVitales;
}

export interface AuditoriaConsulta {
  id: number;
  nombreUsuario: string;
  fechaModificacion: string;
  motivoModificacion: string;
  motivoConsultaAnterior: string | null;
  diagnosticoAnterior: string | null;
  tratamientoAnterior: string | null;
  observacionesAnterior: string | null;
  motivoConsultaNuevo: string | null;
  diagnosticoNuevo: string | null;
  tratamientoNuevo: string | null;
  observacionesNuevo: string | null;
  signosVitalesAnteriores: SignosVitales | null;
  signosVitalesNuevos: SignosVitales | null;
}

export interface CambioAuditado {
  campo: string;
  anterior: string;
  nuevo: string;
}

const ETIQUETAS_SIGNOS: Record<keyof SignosVitales, string> = {
  peso: 'Peso (kg)',
  altura: 'Altura (cm)',
  presionSistolica: 'Presión sistólica (mmHg)',
  presionDiastolica: 'Presión diastólica (mmHg)',
  temperatura: 'Temperatura (°C)',
};

/** Campos que cambiaron entre la versión anterior y la nueva de una consulta. */
export function cambiosDeAuditoria(a: AuditoriaConsulta): CambioAuditado[] {
  const textos: [string, string | null, string | null][] = [
    ['Motivo de consulta', a.motivoConsultaAnterior, a.motivoConsultaNuevo],
    ['Diagnóstico', a.diagnosticoAnterior, a.diagnosticoNuevo],
    ['Tratamiento', a.tratamientoAnterior, a.tratamientoNuevo],
    ['Observaciones', a.observacionesAnterior, a.observacionesNuevo],
  ];
  const cambios = textos
    .filter(([, anterior, nuevo]) => (anterior ?? '') !== (nuevo ?? ''))
    .map(([campo, anterior, nuevo]) => ({ campo, anterior: anterior || '—', nuevo: nuevo || '—' }));

  for (const clave of Object.keys(ETIQUETAS_SIGNOS) as (keyof SignosVitales)[]) {
    const anterior = a.signosVitalesAnteriores?.[clave];
    const nuevo = a.signosVitalesNuevos?.[clave];
    if (anterior !== nuevo) {
      cambios.push({ campo: ETIQUETAS_SIGNOS[clave], anterior: String(anterior ?? '—'), nuevo: String(nuevo ?? '—') });
    }
  }
  return cambios;
}
