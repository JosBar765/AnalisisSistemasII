import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { PacienteService } from '../../services/paciente.service';
import { Paciente, PacienteRequest } from '../../models/paciente.model';
import { PacienteFormComponent } from '../../components/paciente-form/paciente-form.component';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { NombreCompletoPipe, nombreCompleto } from '../../../../shared/pipes/nombre-completo.pipe';

@Component({
  selector: 'app-pacientes-listar',
  standalone: true,
  imports: [RouterLink, PacienteFormComponent, LoadingSpinnerComponent, NombreCompletoPipe],
  templateUrl: './listar.component.html',
})
export class ListarPacientesComponent implements OnInit {
  private readonly pacienteService = inject(PacienteService);

  readonly pacientes = signal<Paciente[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly busqueda = signal('');

  readonly modalAbierto = signal(false);
  readonly pacienteEditando = signal<Paciente | null>(null);
  readonly errorFormulario = signal<string | null>(null);
  readonly guardando = signal(false);

  readonly pacientesFiltrados = computed(() => {
    const texto = this.busqueda().toLowerCase().trim();
    if (!texto) return this.pacientes();
    return this.pacientes().filter((p) =>
      `${p.dpi} ${nombreCompleto(p)} ${p.telefono}`.toLowerCase().includes(texto),
    );
  });

  ngOnInit(): void {
    this.cargarPacientes();
  }

  cargarPacientes(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.pacienteService.listar().subscribe({
      next: (data) => {
        this.pacientes.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los pacientes.');
        this.cargando.set(false);
      },
    });
  }

  abrirNuevo(): void {
    this.pacienteEditando.set(null);
    this.errorFormulario.set(null);
    this.modalAbierto.set(true);
  }

  abrirEditar(paciente: Paciente): void {
    this.pacienteEditando.set(paciente);
    this.errorFormulario.set(null);
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  guardar(request: PacienteRequest): void {
    const editando = this.pacienteEditando();
    const peticion = editando
      ? this.pacienteService.modificar(editando.id, request)
      : this.pacienteService.registrar(request);

    this.guardando.set(true);
    this.errorFormulario.set(null);
    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cerrarModal();
        this.cargarPacientes();
      },
      error: (err: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorFormulario.set(err.error?.message ?? 'No se pudo guardar el paciente.');
      },
    });
  }

  cambiarEstado(paciente: Paciente): void {
    this.pacienteService.cambiarEstado(paciente.id, !paciente.estado).subscribe({
      next: () => this.cargarPacientes(),
      error: () => this.error.set('No se pudo cambiar el estado del paciente.'),
    });
  }
}
