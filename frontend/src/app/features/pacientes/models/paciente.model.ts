export interface Paciente {
  id: number;
  dpi: string;
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido: string;
  fechaNacimiento: string;
  telefono: string;
  correo: string;
  direccion: string;
  estado: boolean;
}

export interface PacienteRequest {
  dpi: string;
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido: string;
  fechaNacimiento: string;
  telefono: string;
  correo: string;
  direccion: string;
  estado?: boolean;
}
