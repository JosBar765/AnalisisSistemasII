import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CitaService } from '../../services/cita.service';
import { Cita, llamadosPendientes, presentesEnEspera } from '../../models/cita.model';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';

/** Pantalla de sala de espera: muestra a quién debe llamar la secretaria según lo que solicitaron los médicos. */
@Component({
  selector: 'app-citas-llamador',
  standalone: true,
  imports: [RouterLink, Hora12Pipe, NombreCompletoPipe],
  templateUrl: './llamador.component.html',
  styleUrl: './llamador.component.css',
})
export class LlamadorComponent implements OnInit {
  private readonly citaService = inject(CitaService);
  private readonly destroyRef = inject(DestroyRef);

  readonly citas = signal<Cita[]>([]);
  readonly horaActual = signal(new Date());

  readonly llamados = computed(() => llamadosPendientes(this.citas()));
  readonly llamadoActual = computed(() => this.llamados()[0] ?? null);
  readonly otrosLlamados = computed(() => this.llamados().slice(1));

  /** Presentes que esperan y aún no tienen un llamado solicitado, en su orden de llegada. */
  readonly siguientes = computed(() =>
    presentesEnEspera(this.citas())
      .filter((c) => !c.horaSolicitudLlamado)
      .map((cita, indice) => ({ cita, turno: indice + 1 })),
  );

  ngOnInit(): void {
    this.cargar();
    this.citaService.cambios$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.cargar());

    const reloj = setInterval(() => this.horaActual.set(new Date()), 1000);
    this.destroyRef.onDestroy(() => clearInterval(reloj));
  }

  private cargar(): void {
    this.citaService.listarAgenda(this.citaService.hoy()).subscribe({ next: (data) => this.citas.set(data) });
  }
}
