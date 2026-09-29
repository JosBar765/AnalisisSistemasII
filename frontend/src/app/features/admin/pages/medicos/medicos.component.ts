import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MedicoService } from '../../services/medico.service';
import { UsuarioService } from '../../services/usuario.service';
import { CatalogoService } from '../../services/catalogo.service';
import { Medico } from '../../models/medico.model';
import { Usuario } from '../../models/usuario.model';
import { Catalogo } from '../../models/catalogo.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-medicos',
  standalone: true,
  imports: [ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './medicos.component.html',
})
export class MedicosComponent implements OnInit {
  private readonly medicoService = inject(MedicoService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly catalogoService = inject(CatalogoService);
  private readonly fb = inject(FormBuilder);

  readonly medicos = signal<Medico[]>([]);
  readonly usuarios = signal<Usuario[]>([]);
  readonly especialidades = signal<Catalogo[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly modalAbierto = signal(false);
  readonly medicoEditando = signal<Medico | null>(null);

  /** Usuarios con rol MEDICO que todavía no tienen un registro de Médico asociado. */
  readonly usuariosDisponibles = computed(() => {
    const idsConMedico = new Set(this.medicos().map((m) => m.id));
    return this.usuarios().filter((u) => u.rol.nombre === 'MEDICO' && !idsConMedico.has(u.id));
  });

  readonly form = this.fb.nonNullable.group({
    idUsuario: [0, [Validators.required, Validators.min(1)]],
    idEspecialidad: [0, [Validators.required, Validators.min(1)]],
    colegiado: ['', Validators.required],
  });

  ngOnInit(): void {
    this.catalogoService.listarEspecialidades().subscribe({ next: (data) => this.especialidades.set(data) });
    this.usuarioService.listar().subscribe({ next: (data) => this.usuarios.set(data) });
    this.cargarMedicos();
  }

  cargarMedicos(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.medicoService.listar().subscribe({
      next: (data) => {
        this.medicos.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los médicos.');
        this.cargando.set(false);
      },
    });
  }

  abrirNuevo(): void {
    this.medicoEditando.set(null);
    this.form.reset({ idUsuario: 0, idEspecialidad: 0, colegiado: '' });
    this.form.controls.idUsuario.enable();
    this.modalAbierto.set(true);
  }

  abrirEditar(medico: Medico): void {
    this.medicoEditando.set(medico);
    this.form.reset({
      idUsuario: medico.id,
      idEspecialidad: medico.especialidadResponseDTO.id,
      colegiado: medico.colegiado,
    });
    this.form.controls.idUsuario.disable();
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

    const valor = this.form.getRawValue();
    const editando = this.medicoEditando();

    const peticion = editando
      ? this.medicoService.modificar(editando.id, {
          idEspecialidad: valor.idEspecialidad,
          colegiado: valor.colegiado,
        })
      : this.medicoService.registrar(valor);

    peticion.subscribe({
      next: () => {
        this.cerrarModal();
        this.cargarMedicos();
      },
      error: () => this.error.set('No se pudo guardar el médico. Verifique que el usuario esté activo.'),
    });
  }

  cambiarEstado(medico: Medico): void {
    const nuevoEstado = !medico.usuarioResponseDTO.estado;
    this.medicoService.cambiarEstado(medico.id, nuevoEstado).subscribe({
      next: () => this.cargarMedicos(),
      error: () => this.error.set('No se pudo cambiar el estado del médico.'),
    });
  }
}
