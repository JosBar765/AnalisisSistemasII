import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ExpedienteService } from '../../services/expediente.service';
import { DocumentoExpediente, Expediente } from '../../models/expediente.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { EdadPipe } from '../../../../shared/pipes/edad.pipe';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';
import { NotificationService } from '../../../../core/services/notification.service';

/** Vista lógica del expediente: datos del paciente, últimos signos vitales, historial de consultas y documentos. */
@Component({
  selector: 'app-expediente-detalle',
  standalone: true,
  imports: [RouterLink, LoadingSpinnerComponent, EdadPipe, Hora12Pipe, NombreCompletoPipe],
  templateUrl: './detalle.component.html',
})
export class DetalleExpedienteComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly route = inject(ActivatedRoute);
  private readonly expedienteService = inject(ExpedienteService);

  readonly expediente = signal<Expediente | null>(null);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  /** La consulta más reciente que tenga signos vitales registrados. */
  readonly ultimaConSignos = computed(
    () => this.expediente()?.historialConsultasResponseDTO.find((c) => !!c.signosVitalesRequestDTO) ?? null,
  );

  ngOnInit(): void {
    const idPaciente = Number(this.route.snapshot.paramMap.get('idPaciente'));
    this.expedienteService.obtener(idPaciente).subscribe({
      next: (data) => {
        this.expediente.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el expediente del paciente.');
        this.cargando.set(false);
      },
    });
  }

  ver(documento: DocumentoExpediente): void {
    this.abrir(documento, false);
  }

  descargar(documento: DocumentoExpediente): void {
    this.abrir(documento, true);
  }

  private abrir(documento: DocumentoExpediente, descarga: boolean): void {
    // La ventana se abre antes de la petición para que el navegador no la bloquee.
    const ventana = descarga ? null : window.open('', '_blank');
    this.expedienteService.obtenerEnlaceDocumento(documento.id).subscribe({
      next: ({ url }) => {
        if (descarga) {
          window.location.assign(`${url}&download=${encodeURIComponent(documento.nombre)}`);
        } else if (ventana) {
          ventana.location.href = url;
        } else {
          window.open(url, '_blank');
        }
      },
      error: (err) => {
        ventana?.close();
        this.notificacion.error(mensajeDeError(err, 'No se pudo abrir el documento.'));
      },
    });
  }
}
