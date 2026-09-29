export type Rol = 'ADMINISTRADOR' | 'SECRETARIA' | 'MEDICO';

export interface UsuarioAuth {
  id: number;
  nombre: string;
  rol: Rol;
}
