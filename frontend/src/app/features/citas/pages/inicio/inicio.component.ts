import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CitaService } from '../../services/cita.service';
import {
  ESTADO_CANCELADO,
  Cita,
  claseEstado,
  estaEnEspera,
  llamadosPendientes,
  presentesEnEspera,
} from '../../models/cita.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';

/** Inicio de recepción: citas de hoy, llegada de pacientes y llamados solicitados por los médicos. */
@Component({
  selector: 'app-citas-inicio',
  standalone: true,
  imports: [RouterLink, LoadingSpinnerComponent, Hora12Pipe, NombreCompletoPipe],
  templateUrl: './inicio.component.html',
})
export class InicioComponent implements OnInit {
  private readonly citaService = inject(CitaService);
  private readonly destroyRef = inject(DestroyRef);

  readonly claseEstado = claseEstado;
  readonly estaEnEspera = estaEnEspera;

  readonly citas = signal<Cita[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  readonly citasVigentes = computed(() =>
    this.citas().filter((c) => c.estadoCitaResponseDTO.nombre !== ESTADO_CANCELADO),
  );
  readonly llamados = computed(() => llamadosPendientes(this.citas()));

  /** Cola del día: primero los presentes en su orden de llegada, luego el resto por hora estimada. */
  readonly cola = computed(() => {
    const presentes = presentesEnEspera(this.citasVigentes());
    const resto = this.citasVigentes().filter((c) => !presentes.includes(c));
    return [
      ...presentes.map((cita, indice) => ({ cita, turno: indice + 1 })),
      ...resto.map((cita) => ({ cita, turno: null })),
    ];
  });

  ngOnInit(): void {
    this.cargar();
    this.citaService.cambios$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.cargar(false));
  }

  cargar(mostrarCarga = true): void {
    if (mostrarCarga) {
      this.cargando.set(true);
    }
    this.error.set(null);
    this.citaService.listarAgenda(this.citaService.hoy()).subscribe({
      next: (data) => {
        this.citas.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la información del día.');
        this.cargando.set(false);
      },
    });
  }

  marcarLlegada(cita: Cita): void {
    this.citaService.registrarLlegada(cita.id).subscribe({
      next: () => this.cargar(false),
      error: (err: HttpErrorResponse) => this.error.set(err.error?.message ?? 'No se pudo registrar la llegada.'),
    });
  }
}
