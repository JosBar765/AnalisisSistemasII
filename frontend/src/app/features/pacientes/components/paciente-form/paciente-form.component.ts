import { Component, OnInit, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Paciente, PacienteRequest } from '../../models/paciente.model';
import { FondoModalDirective } from '../../../../shared/directives/fondo-modal.directive';

/** Modal de registro y edición de datos administrativos del paciente. No guarda: emite la solicitud. */
@Component({
  selector: 'app-paciente-form',
  standalone: true,
  imports: [FondoModalDirective, ReactiveFormsModule],
  templateUrl: './paciente-form.component.html',
})
export class PacienteFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);

  readonly paciente = input<Paciente | null>(null);
  readonly guardando = input(false);

  readonly guardar = output<PacienteRequest>();
  readonly cerrar = output<void>();

  readonly form = this.fb.nonNullable.group({
    dpi: ['', [Validators.required, Validators.maxLength(13)]],
    primerNombre: ['', [Validators.required, Validators.maxLength(50)]],
    segundoNombre: ['', Validators.maxLength(100)],
    primerApellido: ['', [Validators.required, Validators.maxLength(50)]],
    segundoApellido: ['', [Validators.required, Validators.maxLength(50)]],
    fechaNacimiento: ['', Validators.required],
    telefono: ['', [Validators.required, Validators.maxLength(15)]],
    correo: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    direccion: ['', [Validators.required, Validators.maxLength(250)]],
  });

  ngOnInit(): void {
    const paciente = this.paciente();
    if (paciente) {
      this.form.patchValue({ ...paciente, segundoNombre: paciente.segundoNombre ?? '' });
      // El DPI identifica al paciente y no se modifica.
      this.form.controls.dpi.disable();
    }
  }

  enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valor = this.form.getRawValue();
    this.guardar.emit({
      ...valor,
      segundoNombre: valor.segundoNombre || undefined,
      estado: this.paciente()?.estado ?? true,
    });
  }
}
