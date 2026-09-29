import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    correo: ['', [Validators.required, Validators.email]],
    contrasenia: ['', Validators.required],
  });
  readonly error = signal<string | null>(null);
  readonly cargando = signal(false);

  ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.error.set(null);
    this.cargando.set(true);
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        const ruta = this.auth.rutaInicio();
        if (ruta) {
          this.router.navigateByUrl(ruta);
        } else {
          this.auth.logout();
          this.error.set('Tu rol aún no tiene un módulo disponible en el sistema.');
          this.cargando.set(false);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.error.set(this.mensajeDeError(err));
        this.cargando.set(false);
      },
    });
  }

  private mensajeDeError(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'No se pudo conectar con el servidor.';
    }
    return err.error?.message ?? 'No se pudo iniciar sesión.';
  }
}
