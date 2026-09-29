import { Catalogo } from './catalogo.model';
import { Usuario } from './usuario.model';

export interface Medico {
  id: number;
  usuarioResponseDTO: Usuario;
  especialidadResponseDTO: Catalogo;
  colegiado: string;
}

export interface MedicoRequest {
  idUsuario: number;
  idEspecialidad: number;
  colegiado: string;
}
