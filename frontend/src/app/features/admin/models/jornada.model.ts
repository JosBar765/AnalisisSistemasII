import { Catalogo } from './catalogo.model';

export interface JornadaMedica {
  id: number;
  diaSemanaResponseDTO: Catalogo;
  horaInicio: string;
  horaFin: string;
  duracionConsulta: number;
}

export interface JornadaMedicaRequest {
  idMedico: number;
  idDiaSemana: number;
  horaInicio: string;
  horaFin: string;
  duracionConsulta: number;
}
