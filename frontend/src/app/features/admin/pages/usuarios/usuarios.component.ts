import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { CatalogoService } from '../../services/catalogo.service';
import { Usuario } from '../../models/usuario.model';
import { Catalogo } from '../../models/catalogo.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';
import { NotificationService } from '../../../../core/services/notification.service';
import { FondoModalDirective } from '../../../../shared/directives/fondo-modal.directive';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [FondoModalDirective, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './usuarios.component.html',
})
export class UsuariosComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly catalogoService = inject(CatalogoService);
  private readonly fb = inject(FormBuilder);

  readonly usuarios = signal<Usuario[]>([]);
  readonly roles = signal<Catalogo[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly modalAbierto = signal(false);
  readonly usuarioEditando = signal<Usuario | null>(null);
  readonly busqueda = signal('');

  readonly usuariosFiltrados = computed(() => {
    const texto = this.busqueda().toLowerCase().trim();
    if (!texto) return this.usuarios();
    return this.usuarios().filter((u) =>
      `${u.primerNombre} ${u.primerApellido} ${u.correo}`.toLowerCase().includes(texto)
    );
  });

  readonly form = this.fb.nonNullable.group({
    primerNombre: ['', Validators.required],
    segundoNombre: [''],
    primerApellido: ['', Validators.required],
    segundoApellido: ['', Validators.required],
    correo: ['', [Validators.required, Validators.email]],
    telefono: ['', Validators.required],
    contrasenia: [''],
    idRol: [0, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.cargarRoles();
    this.cargarUsuarios();
  }

  cargarRoles(): void {
    this.catalogoService.listarRoles().subscribe({ next: (roles) => this.roles.set(roles) });
  }

  cargarUsuarios(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.usuarioService.listar().subscribe({
      next: (data) => {
        this.usuarios.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los usuarios.');
        this.cargando.set(false);
      },
    });
  }

  abrirNuevo(): void {
    this.usuarioEditando.set(null);
    this.form.reset({ idRol: 0 });
    this.form.controls.contrasenia.setValidators([Validators.required, Validators.minLength(8)]);
    this.form.controls.contrasenia.updateValueAndValidity();
    this.modalAbierto.set(true);
  }

  abrirEditar(usuario: Usuario): void {
    this.usuarioEditando.set(usuario);
    this.form.reset({
      primerNombre: usuario.primerNombre,
      segundoNombre: usuario.segundoNombre ?? '',
      primerApellido: usuario.primerApellido,
      segundoApellido: usuario.segundoApellido ?? '',
      correo: usuario.correo,
      telefono: usuario.telefono,
      contrasenia: '',
      idRol: usuario.rol.id,
    });
    this.form.controls.contrasenia.clearValidators();
    this.form.controls.contrasenia.updateValueAndValidity();
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

    const valor = this.form.getRawValue();
    const editando = this.usuarioEditando();

    const request = {
      idRol: valor.idRol,
      primerNombre: valor.primerNombre,
      segundoNombre: valor.segundoNombre || undefined,
      primerApellido: valor.primerApellido,
      segundoApellido: valor.segundoApellido || undefined,
      correo: valor.correo,
      telefono: valor.telefono,
      contrasenia: valor.contrasenia || undefined,
      estado: editando?.estado ?? true,
    };

    const peticion = editando
      ? this.usuarioService.modificar(editando.id, request)
      : this.usuarioService.registrar(request);

    peticion.subscribe({
      next: () => {
        this.cerrarModal();
        this.cargarUsuarios();
      },
      error: (err) => this.notificacion.error(mensajeDeError(err, 'No se pudo guardar el usuario.')),
    });
  }

  cambiarEstado(usuario: Usuario): void {
    this.usuarioService.cambiarEstado(usuario.id, !usuario.estado).subscribe({
      next: () => this.cargarUsuarios(),
      error: (err) => this.notificacion.error(mensajeDeError(err, 'No se pudo cambiar el estado del usuario.')),
    });
  }
}
