import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AgendaMedicaService } from '../../services/agenda-medica.service';
import {
  CitaMedica,
  ConsultaFinalizada,
  ESTADO_CANCELADO,
  claseEstado,
  estaEnEspera,
  presentesEnEspera,
} from '../../models/agenda-medica.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { EdadPipe } from '../../../../shared/pipes/edad.pipe';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';

/**
 * Agenda del médico (UC-MED-001): sus citas por fecha y la cola de pacientes presentes. El médico
 * elige al siguiente paciente y solicita a la secretaria que lo llame; la hora de la cita es solo estimada.
 */
@Component({
  selector: 'app-agenda-medica',
  standalone: true,
  imports: [RouterLink, LoadingSpinnerComponent, EdadPipe, Hora12Pipe, NombreCompletoPipe],
  templateUrl: './agenda.component.html',
})
export class AgendaMedicaComponent implements OnInit {
  private readonly agendaService = inject(AgendaMedicaService);
  private readonly destroyRef = inject(DestroyRef);

  readonly claseEstado = claseEstado;
  readonly estaEnEspera = estaEnEspera;

  readonly fecha = signal(this.agendaService.hoy());
  readonly citas = signal<CitaMedica[]>([]);
  readonly consultas = signal<ConsultaFinalizada[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  readonly esHoy = computed(() => this.fecha() === this.agendaService.hoy());
  readonly citasVigentes = computed(() =>
    this.citas().filter((c) => c.estadoCitaResponseDTO.nombre !== ESTADO_CANCELADO),
  );
  readonly cola = computed(() => presentesEnEspera(this.citasVigentes()));
  readonly llamados = computed(() => this.cola().filter((c) => !!c.horaSolicitudLlamado));
  /** Siguiente paciente por orden de llegada al que todavía no se le ha solicitado el llamado. */
  readonly siguiente = computed(() => this.cola().find((c) => !c.horaSolicitudLlamado) ?? null);

  ngOnInit(): void {
    this.cargar();
    this.agendaService.cambios$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.cargar(false));
  }

  cambiarFecha(fecha: string): void {
    if (fecha) {
      this.fecha.set(fecha);
      this.cargar();
    }
  }

  cargar(mostrarCarga = true): void {
    if (mostrarCarga) {
      this.cargando.set(true);
    }
    this.error.set(null);
    const fecha = this.fecha();
    this.agendaService.listarMisCitas(fecha).subscribe({
      next: (data) => {
        this.citas.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar su agenda.');
        this.cargando.set(false);
      },
    });
    this.agendaService.listarMisConsultas(fecha).subscribe({ next: (data) => this.consultas.set(data) });
  }

  solicitarLlamado(cita: CitaMedica): void {
    this.agendaService.solicitarLlamado(cita.id).subscribe({
      next: () => this.cargar(false),
      error: (err: HttpErrorResponse) => this.error.set(err.error?.message ?? 'No se pudo solicitar el llamado.'),
    });
  }
}
