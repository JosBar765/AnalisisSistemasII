import { Component, OnInit, computed, inject, input, output, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CitaService } from '../../services/cita.service';
import { Cita, ESTADO_ATENDIDO, MedicoCita, PacienteCita } from '../../models/cita.model';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NombreCompletoPipe, nombreCompleto } from '../../../../shared/pipes/nombre-completo.pipe';
import { NotificationService } from '../../../../core/services/notification.service';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';
import { FondoModalDirective } from '../../../../shared/directives/fondo-modal.directive';
import { coincideBusqueda } from '../../../../shared/utils/busqueda';

/**
 * Modal para programar una cita nueva o reprogramar una existente (si recibe `cita`).
 * Los horarios ofrecidos los calcula el Backend con la jornada del médico y las citas existentes.
 */
@Component({
  selector: 'app-cita-form-modal',
  standalone: true,
  imports: [FondoModalDirective, ReactiveFormsModule, Hora12Pipe, NombreCompletoPipe],
  templateUrl: './cita-form-modal.component.html',
  styleUrl: './cita-form-modal.component.css',
})
export class CitaFormModalComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly citaService = inject(CitaService);
  private readonly fb = inject(FormBuilder);

  readonly cita = input<Cita | null>(null);
  readonly esSeguimiento = computed(() => this.cita()?.estadoCitaResponseDTO.nombre === ESTADO_ATENDIDO);
  readonly fechaInicial = input<string>('');
  readonly idPacienteInicial = input<number | null>(null);

  readonly guardada = output<void>();
  readonly cerrar = output<void>();

  readonly pacientes = signal<PacienteCita[]>([]);
  readonly medicos = signal<MedicoCita[]>([]);
  readonly filtroPaciente = signal('');
  readonly horarios = signal<string[]>([]);
  readonly cargandoHorarios = signal(false);
  readonly horaSeleccionada = signal<string | null>(null);
  readonly guardando = signal(false);

  readonly pacientesFiltrados = computed(() => {
    return this.pacientes().filter(
      (p) => p.estado && coincideBusqueda(`${p.dpi} ${nombreCompleto(p)}`, this.filtroPaciente()),
    );
  });

  readonly form = this.fb.group({
    idPaciente: this.fb.control<number | null>(null, Validators.required),
    idMedico: this.fb.control<number | null>(null, Validators.required),
    fecha: this.fb.nonNullable.control('', Validators.required),
  });

  ngOnInit(): void {
    const cita = this.cita();
    if (cita) {
      this.form.patchValue({
        idPaciente: cita.pacienteResponseDTO.id,
        idMedico: cita.medicoResponseDTO.id,
        fecha: cita.fecha,
      });
      this.cargarHorarios();
      return;
    }

    this.form.patchValue({ fecha: this.fechaInicial() || this.citaService.hoy(), idPaciente: this.idPacienteInicial() });
    this.citaService.listarPacientes().subscribe({ next: (data) => this.pacientes.set(data) });
    this.citaService.listarMedicos().subscribe({
      next: (data) => this.medicos.set(data.filter((m) => m.usuarioResponseDTO.estado)),
    });
  }

  cargarHorarios(): void {
    const { idMedico, fecha } = this.form.getRawValue();
    this.horaSeleccionada.set(null);
    this.horarios.set([]);
    if (!idMedico || !fecha) {
      return;
    }

    this.cargandoHorarios.set(true);
    this.citaService.listarHorarios(idMedico, fecha).subscribe({
      next: (data) => {
        this.horarios.set(data.map((h) => h.hora));
        this.cargandoHorarios.set(false);
      },
      error: () => {
        this.notificacion.error('No se pudieron consultar los horarios disponibles.');
        this.cargandoHorarios.set(false);
      },
    });
  }

  guardar(): void {
    const { idPaciente, idMedico, fecha } = this.form.getRawValue();
    const hora = this.horaSeleccionada();
    if (!idPaciente || !idMedico || !fecha || !hora) {
      return;
    }

    const request = { idPaciente, idMedico, fecha, hora };
    const cita = this.cita();
    const peticion = cita ? this.citaService.reprogramar(cita.id, request) : this.citaService.programar(request);

    this.guardando.set(true);
    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.guardada.emit();
      },
      error: (err: HttpErrorResponse) => {
        this.guardando.set(false);
        this.notificacion.error(mensajeDeError(err, 'No se pudo guardar la cita.'));
        this.cargarHorarios();
      },
    });
  }
}
