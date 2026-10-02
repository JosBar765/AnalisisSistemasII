import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoriaDocumentoService } from '../../services/categoria-documento.service';
import { Catalogo } from '../../models/catalogo.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';
import { NotificationService } from '../../../../core/services/notification.service';
import { FondoModalDirective } from '../../../../shared/directives/fondo-modal.directive';

/** Catálogo de categorías con las que se clasifican los documentos clínicos (UC-SEC-002). */
@Component({
  selector: 'app-categorias-documento',
  standalone: true,
  imports: [FondoModalDirective, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './categorias-documento.component.html',
})
export class CategoriasDocumentoComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly categoriaService = inject(CategoriaDocumentoService);
  private readonly fb = inject(FormBuilder);

  readonly categorias = signal<Catalogo[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly modalAbierto = signal(false);
  readonly categoriaEditando = signal<Catalogo | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
  });

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.categoriaService.listar().subscribe({
      next: (data) => {
        this.categorias.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las categorías de documentos.');
        this.cargando.set(false);
      },
    });
  }

  abrirNueva(): void {
    this.categoriaEditando.set(null);
    this.form.reset();
    this.modalAbierto.set(true);
  }

  abrirEditar(categoria: Catalogo): void {
    this.categoriaEditando.set(categoria);
    this.form.reset({ nombre: categoria.nombre });
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const nombre = this.form.getRawValue().nombre.trim();
    const editando = this.categoriaEditando();
    const peticion = editando
      ? this.categoriaService.editar(editando.id, nombre)
      : this.categoriaService.registrar(nombre);

    peticion.subscribe({
      next: () => {
        this.cerrarModal();
        this.cargarCategorias();
      },
      error: (err) => this.notificacion.error(mensajeDeError(err, 'No se pudo guardar la categoría.')),
    });
  }

  eliminar(categoria: Catalogo): void {
    this.categoriaService.eliminar(categoria.id).subscribe({
      next: () => this.cargarCategorias(),
      error: (err) => this.notificacion.error(mensajeDeError(err, 'No se pudo eliminar la categoría.')),
    });
  }
}
