import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CitaService } from '../../services/cita.service';
import { Cita, MedicoCita, claseEstado, estaEnEspera } from '../../models/cita.model';
import { CitaFormModalComponent } from '../../components/cita-form-modal/cita-form-modal.component';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';
import { NotificationService } from '../../../../core/services/notification.service';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';
import { FondoModalDirective } from '../../../../shared/directives/fondo-modal.directive';

@Component({
  selector: 'app-citas-agenda',
  standalone: true,
  imports: [FondoModalDirective, CitaFormModalComponent, LoadingSpinnerComponent, Hora12Pipe, NombreCompletoPipe],
  templateUrl: './agenda.component.html',
})
export class AgendaComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly citaService = inject(CitaService);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  readonly claseEstado = claseEstado;
  readonly estaEnEspera = estaEnEspera;

  readonly modo = signal<'dia' | 'mes'>('dia');
  readonly fecha = signal(this.citaService.hoy());
  /** Mes consultado en modo "mes", con formato AAAA-MM. */
  readonly mes = signal(this.citaService.hoy().slice(0, 7));
  readonly etiquetaMes = computed(() => {
    const [anio, mes] = this.mes().split('-').map(Number);
    const texto = new Date(anio, mes - 1, 1).toLocaleDateString('es-GT', { month: 'long', year: 'numeric' });
    return texto.charAt(0).toUpperCase() + texto.slice(1);
  });
  readonly medicoFiltro = signal<number | null>(null);
  readonly citas = signal<Cita[]>([]);
  readonly medicos = signal<MedicoCita[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  readonly modalAbierto = signal(false);
  readonly citaReprogramando = signal<Cita | null>(null);
  readonly idPacienteInicial = signal<number | null>(null);
  readonly citaPorCancelar = signal<Cita | null>(null);

  readonly citasFiltradas = computed(() => {
    const idMedico = this.medicoFiltro();
    return idMedico ? this.citas().filter((c) => c.medicoResponseDTO.id === idMedico) : this.citas();
  });

  ngOnInit(): void {
    this.citaService.listarMedicos().subscribe({ next: (data) => this.medicos.set(data) });
    this.cargarAgenda();
    this.citaService.cambios$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.cargarAgenda(false));

    // Se llega desde Pacientes ("Programar Cita") o desde Inicio ("Agendar Nueva Cita").
    const params = this.route.snapshot.queryParamMap;
    if (params.has('idPaciente') || params.has('nueva')) {
      this.idPacienteInicial.set(params.has('idPaciente') ? Number(params.get('idPaciente')) : null);
      this.abrirNueva();
    }
  }

  cambiarModo(modo: 'dia' | 'mes'): void {
    this.modo.set(modo);
    this.cargarAgenda();
  }

  /** Avanza (+1) o retrocede (-1) un mes. */
  cambiarMes(delta: number): void {
    const [anio, mes] = this.mes().split('-').map(Number);
    const nuevo = new Date(anio, mes - 1 + delta, 1);
    this.mes.set(`${nuevo.getFullYear()}-${String(nuevo.getMonth() + 1).padStart(2, '0')}`);
    this.cargarAgenda();
  }

  cambiarFecha(fecha: string): void {
    if (fecha) {
      this.fecha.set(fecha);
      this.cargarAgenda();
    }
  }

  cargarAgenda(mostrarCarga = true): void {
    if (mostrarCarga) {
      this.cargando.set(true);
    }
    this.error.set(null);
    this.consultarAgenda().subscribe({
      next: (data) => {
        this.citas.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la agenda.');
        this.cargando.set(false);
      },
    });
  }

  private consultarAgenda(): Observable<Cita[]> {
    if (this.modo() === 'dia') {
      return this.citaService.listarAgenda(this.fecha());
    }
    const [anio, mes] = this.mes().split('-').map(Number);
    const ultimoDia = new Date(anio, mes, 0).getDate();
    return this.citaService.listarAgendaRango(`${this.mes()}-01`, `${this.mes()}-${String(ultimoDia).padStart(2, '0')}`);
  }

  abrirNueva(): void {
    this.citaReprogramando.set(null);
    this.modalAbierto.set(true);
  }

  abrirReprogramar(cita: Cita): void {
    this.citaReprogramando.set(cita);
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    this.modalAbierto.set(false);
    this.idPacienteInicial.set(null);
  }

  alGuardar(): void {
    this.cerrarModal();
    this.cargarAgenda(false);
  }

  confirmarCancelacion(): void {
    const cita = this.citaPorCancelar();
    if (!cita) {
      return;
    }
    this.citaService.cancelar(cita.id).subscribe({
      next: () => {
        this.citaPorCancelar.set(null);
        this.cargarAgenda(false);
      },
      error: (err: HttpErrorResponse) => {
        this.citaPorCancelar.set(null);
        this.notificacion.error(mensajeDeError(err, 'No se pudo cancelar la cita.'));
      },
    });
  }
}
