import { Component, OnInit, inject, signal } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { Dashboard } from '../../models/dashboard.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LoadingSpinnerComponent],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  readonly dashboard = signal<Dashboard | null>(null);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarMetricas();
  }

  cargarMetricas(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.dashboardService.obtenerMetricas().subscribe({
      next: (data) => {
        this.dashboard.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los indicadores del dashboard.');
        this.cargando.set(false);
      },
    });
  }
}
