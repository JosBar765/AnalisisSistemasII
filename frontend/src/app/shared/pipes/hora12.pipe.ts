import { Pipe, PipeTransform } from '@angular/core';

/** Convierte una hora "HH:mm" o "HH:mm:ss" del Backend a formato de 12 horas ("10:30 AM"). */
export function hora12(hora: string | null | undefined): string {
  if (!hora) {
    return '';
  }
  const [h, m] = hora.split(':').map(Number);
  const periodo = h >= 12 ? 'PM' : 'AM';
  const hora12 = h % 12 === 0 ? 12 : h % 12;
  return `${String(hora12).padStart(2, '0')}:${String(m).padStart(2, '0')} ${periodo}`;
}

@Pipe({ name: 'hora12', standalone: true })
export class Hora12Pipe implements PipeTransform {
  transform(hora: string | null | undefined): string {
    return hora12(hora);
  }
}
