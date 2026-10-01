import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EspecialidadService } from '../../services/especialidad.service';
import { Catalogo } from '../../models/catalogo.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';
import { NotificationService } from '../../../../core/services/notification.service';
import { FondoModalDirective } from '../../../../shared/directives/fondo-modal.directive';

@Component({
  selector: 'app-especialidades',
  standalone: true,
  imports: [FondoModalDirective, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './especialidades.component.html',
})
export class EspecialidadesComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly especialidadService = inject(EspecialidadService);
  private readonly fb = inject(FormBuilder);

  readonly especialidades = signal<Catalogo[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly modalAbierto = signal(false);
  readonly especialidadEditando = signal<Catalogo | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', Validators.required],
  });

  ngOnInit(): void {
    this.cargarEspecialidades();
  }

  cargarEspecialidades(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.especialidadService.listar().subscribe({
      next: (data) => {
        this.especialidades.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las especialidades.');
        this.cargando.set(false);
      },
    });
  }

  abrirNueva(): void {
    this.especialidadEditando.set(null);
    this.form.reset();
    this.modalAbierto.set(true);
  }

  abrirEditar(especialidad: Catalogo): void {
    this.especialidadEditando.set(especialidad);
    this.form.reset({ nombre: especialidad.nombre });
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const nombre = this.form.getRawValue().nombre;
    const editando = this.especialidadEditando();
    const peticion = editando
      ? this.especialidadService.editar(editando.id, nombre)
      : this.especialidadService.registrar(nombre);

    peticion.subscribe({
      next: () => {
        this.cerrarModal();
        this.cargarEspecialidades();
      },
      error: (err) => this.notificacion.error(mensajeDeError(err, 'No se pudo guardar la especialidad.')),
    });
  }

  eliminar(especialidad: Catalogo): void {
    this.error.set(null);
    this.especialidadService.eliminar(especialidad.id).subscribe({
      next: () => this.cargarEspecialidades(),
      error: (err) => this.notificacion.error(mensajeDeError(err, 'No se pudo eliminar la especialidad.')),
    });
  }
}
