import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css',
})
export class MainLayoutComponent {
  private readonly auth = inject(AuthService);

  readonly usuario = this.auth.usuario;
  readonly iniciales = computed(() =>
    (this.usuario()?.nombre ?? '')
      .split(' ')
      .map((palabra) => palabra.charAt(0))
      .join('')
      .toUpperCase(),
  );

  cerrarSesion(): void {
    this.auth.logout();
  }
}
