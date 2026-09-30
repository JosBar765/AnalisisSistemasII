import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { DocumentoService } from '../../services/documento.service';
import { AuditoriaDocumento, Documento, PacienteDocumento } from '../../models/documento.model';
import { SubirDocumentoModalComponent } from '../../components/subir-documento-modal/subir-documento-modal.component';
import { ReemplazarDocumentoModalComponent } from '../../components/reemplazar-documento-modal/reemplazar-documento-modal.component';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';

@Component({
  selector: 'app-documentos-listar',
  standalone: true,
  imports: [
    DatePipe,
    NombreCompletoPipe,
    LoadingSpinnerComponent,
    SubirDocumentoModalComponent,
    ReemplazarDocumentoModalComponent,
  ],
  templateUrl: './listar.component.html',
})
export class ListarDocumentosComponent implements OnInit {
  private readonly documentoService = inject(DocumentoService);
  private readonly route = inject(ActivatedRoute);

  readonly pacientes = signal<PacienteDocumento[]>([]);
  readonly idPaciente = signal<number | null>(null);
  readonly documentos = signal<Documento[]>([]);
  readonly auditorias = signal<AuditoriaDocumento[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  readonly subiendo = signal(false);
  readonly documentoReemplazando = signal<Documento | null>(null);

  readonly paciente = computed(() => this.pacientes().find((p) => p.id === this.idPaciente()) ?? null);

  ngOnInit(): void {
    this.documentoService.listarPacientes().subscribe({
      next: (data) => {
        this.pacientes.set(data);
        // Se llega desde Pacientes ("Documentos") con el paciente ya elegido.
        const idInicial = Number(this.route.snapshot.queryParamMap.get('idPaciente'));
        if (idInicial && data.some((p) => p.id === idInicial)) {
          this.seleccionarPaciente(idInicial);
        }
      },
      error: () => this.error.set('No se pudieron cargar los pacientes.'),
    });
  }

  seleccionarPaciente(id: number | null): void {
    this.idPaciente.set(id);
    this.documentos.set([]);
    this.auditorias.set([]);
    if (id) {
      this.cargarDocumentos();
    }
  }

  cargarDocumentos(): void {
    const id = this.idPaciente();
    if (!id) {
      return;
    }
    this.cargando.set(true);
    this.error.set(null);
    this.documentoService.listarPorPaciente(id).subscribe({
      next: (data) => {
        this.documentos.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los documentos.');
        this.cargando.set(false);
      },
    });
    this.documentoService.listarAuditoriaPorPaciente(id).subscribe({ next: (data) => this.auditorias.set(data) });
  }

  alTerminarCarga(): void {
    this.subiendo.set(false);
    this.documentoReemplazando.set(null);
    this.cargarDocumentos();
  }

  ver(documento: Documento): void {
    // La ventana se abre antes de la petición para que el navegador no la bloquee.
    const ventana = window.open('', '_blank');
    this.documentoService.obtenerEnlace(documento.id).subscribe({
      next: ({ url }) => {
        if (ventana) {
          ventana.location.href = url;
        } else {
          window.open(url, '_blank');
        }
      },
      error: () => {
        ventana?.close();
        this.error.set('No se pudo abrir el documento.');
      },
    });
  }
}
