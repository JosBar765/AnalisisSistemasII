import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ExpedienteService } from '../../services/expediente.service';
import { PacienteExpediente } from '../../models/expediente.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { EdadPipe } from '../../../../shared/pipes/edad.pipe';
import { NombreCompletoPipe, nombreCompleto } from '../../../../shared/pipes/nombre-completo.pipe';

/** Buscador de pacientes para abrir su expediente clínico (UC-MED-002). */
@Component({
  selector: 'app-expediente-buscar',
  standalone: true,
  imports: [RouterLink, LoadingSpinnerComponent, EdadPipe, NombreCompletoPipe],
  templateUrl: './buscar.component.html',
})
export class BuscarExpedienteComponent implements OnInit {
  private readonly expedienteService = inject(ExpedienteService);

  readonly pacientes = signal<PacienteExpediente[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly busqueda = signal('');

  readonly pacientesFiltrados = computed(() => {
    const texto = this.busqueda().toLowerCase().trim();
    if (!texto) return this.pacientes();
    return this.pacientes().filter((p) => `${p.dpi} ${nombreCompleto(p)} ${p.telefono}`.toLowerCase().includes(texto));
  });

  ngOnInit(): void {
    this.expedienteService.listarPacientes().subscribe({
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
}
