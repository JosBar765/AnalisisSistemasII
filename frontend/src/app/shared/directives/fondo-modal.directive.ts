import { Directive, ElementRef, inject, output } from '@angular/core';

/**
 * Se aplica al fondo oscuro de un modal (`.modal-backdrop`). Emite `fondoClick` solo si el clic **empezó y terminó**
 * sobre el fondo. Un `(click)` normal también se dispara cuando se presiona el botón dentro del formulario (p. ej.
 * al seleccionar texto de un campo) y se suelta fuera de él: el navegador atribuye el clic al ancestro común,
 * que es el fondo, y el modal se cerraba por error.
 */
@Directive({
  selector: '[appFondoModal]',
  standalone: true,
  host: {
    '(mousedown)': 'registrarInicio($event)',
    '(click)': 'confirmarFin($event)',
  },
})
export class FondoModalDirective {
  private readonly fondo = inject<ElementRef<HTMLElement>>(ElementRef).nativeElement;
  private inicioEnElFondo = false;

  readonly fondoClick = output<void>();

  registrarInicio(evento: MouseEvent): void {
    this.inicioEnElFondo = evento.target === this.fondo;
  }

  confirmarFin(evento: MouseEvent): void {
    if (this.inicioEnElFondo && evento.target === this.fondo) {
      this.fondoClick.emit();
    }
    this.inicioEnElFondo = false;
  }
}
