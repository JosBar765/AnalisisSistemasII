import { Component, OnInit, inject, input, output, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DocumentoService } from '../../services/documento.service';
import { CatalogoDocumento, EXTENSIONES_PERMITIDAS, PacienteDocumento } from '../../models/documento.model';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';

@Component({
  selector: 'app-subir-documento-modal',
  standalone: true,
  imports: [NombreCompletoPipe],
  templateUrl: './subir-documento-modal.component.html',
})
export class SubirDocumentoModalComponent implements OnInit {
  private readonly documentoService = inject(DocumentoService);

  readonly paciente = input.required<PacienteDocumento>();

  readonly subido = output<void>();
  readonly cerrar = output<void>();

  readonly extensiones = EXTENSIONES_PERMITIDAS;
  readonly categorias = signal<CatalogoDocumento[]>([]);
  readonly idCategoria = signal<number | null>(null);
  readonly archivo = signal<File | null>(null);
  readonly error = signal<string | null>(null);
  readonly subiendo = signal(false);

  ngOnInit(): void {
    this.documentoService.listarCategorias().subscribe({ next: (data) => this.categorias.set(data) });
  }

  seleccionarArchivo(evento: Event): void {
    this.archivo.set((evento.target as HTMLInputElement).files?.[0] ?? null);
  }

  subir(): void {
    const archivo = this.archivo();
    const idCategoria = this.idCategoria();
    if (!archivo || !idCategoria) {
      return;
    }

    this.subiendo.set(true);
    this.error.set(null);
    this.documentoService.subir(this.paciente().id, idCategoria, archivo).subscribe({
      next: () => {
        this.subiendo.set(false);
        this.subido.emit();
      },
      error: (err: HttpErrorResponse) => {
        this.subiendo.set(false);
        this.error.set(err.error?.message ?? 'No se pudo subir el documento.');
      },
    });
  }
}
