import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Login sin validación (bypass intencional para esta fase): al enviar el
 * formulario se redirige directo a /admin/dashboard sin llamar al Backend
 * ni pedir token. Ver core/services/auth.service.ts para el login real
 * cuando se conecte la autenticación basada en JWT.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly router = inject(Router);

  ingresar(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}
