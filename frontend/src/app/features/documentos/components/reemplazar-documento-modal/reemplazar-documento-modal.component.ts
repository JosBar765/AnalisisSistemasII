import { Component, OnInit, inject, input, output, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DocumentoService } from '../../services/documento.service';
import { CatalogoDocumento, Documento, EXTENSIONES_PERMITIDAS } from '../../models/documento.model';
import { NotificationService } from '../../../../core/services/notification.service';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';
import { FondoModalDirective } from '../../../../shared/directives/fondo-modal.directive';

/** Reemplaza el archivo de un documento. El motivo es obligatorio porque genera la auditoría. */
@Component({
  selector: 'app-reemplazar-documento-modal',
  standalone: true,
  imports: [FondoModalDirective],
  templateUrl: './reemplazar-documento-modal.component.html',
})
export class ReemplazarDocumentoModalComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly documentoService = inject(DocumentoService);

  readonly documento = input.required<Documento>();

  readonly reemplazado = output<void>();
  readonly cerrar = output<void>();

  readonly extensiones = EXTENSIONES_PERMITIDAS;
  readonly categorias = signal<CatalogoDocumento[]>([]);
  readonly motivos = signal<CatalogoDocumento[]>([]);
  readonly idMotivo = signal<number | null>(null);
  readonly idCategoria = signal<number | null>(null);
  readonly archivo = signal<File | null>(null);
  readonly guardando = signal(false);

  ngOnInit(): void {
    this.documentoService.listarCategorias().subscribe({ next: (data) => this.categorias.set(data) });
    this.documentoService.listarMotivos().subscribe({ next: (data) => this.motivos.set(data) });
  }

  seleccionarArchivo(evento: Event): void {
    this.archivo.set((evento.target as HTMLInputElement).files?.[0] ?? null);
  }

  guardar(): void {
    const archivo = this.archivo();
    const idMotivo = this.idMotivo();
    if (!archivo || !idMotivo) {
      return;
    }

    this.guardando.set(true);
    this.documentoService
      .reemplazar(this.documento().id, archivo, idMotivo, this.idCategoria() ?? undefined)
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.reemplazado.emit();
        },
        error: (err: HttpErrorResponse) => {
          this.guardando.set(false);
          this.notificacion.error(mensajeDeError(err, 'No se pudo actualizar el documento.'));
        },
      });
  }
}
