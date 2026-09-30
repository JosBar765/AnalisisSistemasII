import { Pipe, PipeTransform } from '@angular/core';

export interface Persona {
  primerNombre: string;
  segundoNombre?: string | null;
  primerApellido: string;
  segundoApellido?: string | null;
}

export function nombreCompleto(persona: Persona | null | undefined): string {
  if (!persona) {
    return '';
  }
  return [persona.primerNombre, persona.segundoNombre, persona.primerApellido, persona.segundoApellido]
    .filter((parte) => !!parte)
    .join(' ');
}

@Pipe({ name: 'nombreCompleto', standalone: true })
export class NombreCompletoPipe implements PipeTransform {
  transform(persona: Persona | null | undefined): string {
    return nombreCompleto(persona);
  }
}
