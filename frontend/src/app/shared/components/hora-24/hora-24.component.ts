import { Component, forwardRef, signal } from '@angular/core';
import { AbstractControl, ControlValueAccessor, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors, Validator } from '@angular/forms';

const FORMATO_COMPLETO = /^([01]\d|2[0-3]):[0-5]\d$/;

/**
 * Campo único de hora en formato de 24 horas ("HH:mm", de 00:00 a 23:59). `<input type="time">` muestra AM/PM según
 * el idioma del navegador y no se puede forzar, por eso es un campo de texto con máscara: solo admite dígitos y
 * coloca los dos puntos solo ("0830" → "08:30"). Funciona con formularios reactivos: el valor es "HH:mm" y,
 * mientras la hora esté incompleta o sea inválida, el control es inválido (error `hora24`).
 */
@Component({
  selector: 'app-hora-24',
  standalone: true,
  providers: [
    { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => Hora24Component), multi: true },
    { provide: NG_VALIDATORS, useExisting: forwardRef(() => Hora24Component), multi: true },
  ],
  template: `
    <input
      type="text"
      class="form-control"
      inputmode="numeric"
      autocomplete="off"
      maxlength="5"
      placeholder="HH:mm (24 h)"
      [value]="texto()"
      [disabled]="deshabilitado()"
      (input)="escribir($any($event.target))"
      (blur)="alTocar()"
    />
  `,
})
export class Hora24Component implements ControlValueAccessor, Validator {
  readonly texto = signal('');
  readonly deshabilitado = signal(false);

  private alCambiar: (valor: string) => void = () => {};
  alTocar: () => void = () => {};

  writeValue(valor: string | null): void {
    this.texto.set((valor ?? '').slice(0, 5));
  }

  registerOnChange(fn: (valor: string) => void): void {
    this.alCambiar = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.alTocar = fn;
  }

  setDisabledState(deshabilitado: boolean): void {
    this.deshabilitado.set(deshabilitado);
  }

  validate(control: AbstractControl): ValidationErrors | null {
    const valor = control.value as string | null;
    return !valor || FORMATO_COMPLETO.test(valor) ? null : { hora24: true };
  }

  escribir(input: HTMLInputElement): void {
    const digitos = input.value.replace(/\D/g, '').slice(0, 4);
    const texto = digitos.length > 2 ? `${digitos.slice(0, 2)}:${digitos.slice(2)}` : digitos;
    input.value = texto; // corrige lo escrito (letras, más de 4 dígitos) en el propio campo
    this.texto.set(texto);
    this.alCambiar(texto);
  }
}
