import { Component, OnInit, inject, input, output, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DocumentoService } from '../../services/documento.service';
import { CatalogoDocumento, Documento, EXTENSIONES_PERMITIDAS } from '../../models/documento.model';

/** Reemplaza el archivo de un documento. El motivo es obligatorio porque genera la auditoría. */
@Component({
  selector: 'app-reemplazar-documento-modal',
  standalone: true,
  templateUrl: './reemplazar-documento-modal.component.html',
})
export class ReemplazarDocumentoModalComponent implements OnInit {
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
  readonly error = signal<string | null>(null);
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
    this.error.set(null);
    this.documentoService
      .reemplazar(this.documento().id, archivo, idMotivo, this.idCategoria() ?? undefined)
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.reemplazado.emit();
        },
        error: (err: HttpErrorResponse) => {
          this.guardando.set(false);
          this.error.set(err.error?.message ?? 'No se pudo actualizar el documento.');
        },
      });
  }
}
