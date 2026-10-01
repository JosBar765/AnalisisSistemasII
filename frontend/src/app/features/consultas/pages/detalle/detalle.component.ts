import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ConsultaService } from '../../services/consulta.service';
import {
  AuditoriaConsulta,
  CatalogoConsulta,
  Consulta,
  ModificarConsultaRequest,
  cambiosDeAuditoria,
} from '../../models/consulta.model';
import { ModificarConsultaModalComponent } from '../../components/modificar-consulta-modal/modificar-consulta-modal.component';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NotificationService } from '../../../../core/services/notification.service';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';

/** Consulta finalizada (UC-MED-004): datos actuales y bitácora de modificaciones. Solo el médico que la atendió la corrige. */
@Component({
  selector: 'app-consulta-detalle',
  standalone: true,
  imports: [RouterLink, LoadingSpinnerComponent, ModificarConsultaModalComponent, Hora12Pipe],
  templateUrl: './detalle.component.html',
})
export class DetalleConsultaComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(AuthService);
  private readonly consultaService = inject(ConsultaService);

  readonly cambiosDeAuditoria = cambiosDeAuditoria;

  readonly consulta = signal<Consulta | null>(null);
  readonly auditorias = signal<AuditoriaConsulta[]>([]);
  readonly motivos = signal<CatalogoConsulta[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  readonly modalAbierto = signal(false);
  readonly guardando = signal(false);

  readonly puedeModificar = computed(() => this.consulta()?.idMedico === this.auth.usuario()?.id);

  ngOnInit(): void {
    this.cargar();
    this.consultaService.listarMotivosModificacion().subscribe({ next: (data) => this.motivos.set(data) });
  }

  private get id(): number {
    return Number(this.route.snapshot.paramMap.get('id'));
  }

  cargar(): void {
    this.consultaService.obtener(this.id).subscribe({
      next: (data) => {
        this.consulta.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se encontró la consulta.');
        this.cargando.set(false);
      },
    });
    this.consultaService.listarAuditoria(this.id).subscribe({ next: (data) => this.auditorias.set(data) });
  }

  abrirModal(): void {
    this.modalAbierto.set(true);
  }

  guardar(request: ModificarConsultaRequest): void {
    this.guardando.set(true);
    this.consultaService.modificar(this.id, request).subscribe({
      next: () => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.cargar();
      },
      error: (err: HttpErrorResponse) => {
        this.notificacion.error(mensajeDeError(err, 'No se pudo modificar la consulta.'));
        this.guardando.set(false);
      },
    });
  }
}
