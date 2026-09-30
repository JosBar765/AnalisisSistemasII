import { Pipe, PipeTransform } from '@angular/core';

/** Edad en años cumplidos a partir de una fecha "AAAA-MM-DD". */
export function calcularEdad(fechaNacimiento: string | null | undefined): number | null {
  if (!fechaNacimiento) {
    return null;
  }
  const nacimiento = new Date(`${fechaNacimiento}T00:00:00`);
  const hoy = new Date();
  let edad = hoy.getFullYear() - nacimiento.getFullYear();
  const aunNoCumple =
    hoy.getMonth() < nacimiento.getMonth() ||
    (hoy.getMonth() === nacimiento.getMonth() && hoy.getDate() < nacimiento.getDate());
  if (aunNoCumple) {
    edad--;
  }
  return edad;
}

@Pipe({ name: 'edad', standalone: true })
export class EdadPipe implements PipeTransform {
  transform(fechaNacimiento: string | null | undefined): string {
    const edad = calcularEdad(fechaNacimiento);
    return edad === null ? '' : `${edad} años`;
  }
}
