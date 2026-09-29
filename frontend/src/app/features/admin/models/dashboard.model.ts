export interface ConsultaPorMedico {
  idMedico: number;
  nombreMedico: string;
  cantidad: number;
}

export interface Dashboard {
  pacientesRegistrados: number;
  consultasRealizadas: number;
  citasCanceladas: number;
  consultasPorMedico: ConsultaPorMedico[];
}
