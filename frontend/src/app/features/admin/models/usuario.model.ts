import { Catalogo } from './catalogo.model';

export interface Usuario {
  id: number;
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido?: string;
  correo: string;
  telefono: string;
  estado: boolean;
  fechaCreacion: string;
  rol: Catalogo;
}

export interface UsuarioRequest {
  idRol: number;
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido?: string;
  correo: string;
  telefono: string;
  contrasenia?: string;
  estado?: boolean;
}
