import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Rol } from '../../core/models/usuario-auth.model';
import { AuthService } from '../../core/services/auth.service';
import { PerfilMedicoService } from '../../core/services/perfil-medico.service';

const PANEL: Record<Rol, string> = {
  ADMINISTRADOR: 'Panel Admin',
  SECRETARIA: 'Recepción',
  MEDICO: 'Panel Médico',
};

const ROL: Record<Rol, string> = {
  ADMINISTRADOR: 'Administrador',
  SECRETARIA: 'Secretaria',
  MEDICO: 'Médico',
};

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
  readonly especialidad = inject(PerfilMedicoService).especialidad;
  readonly esAdmin = computed(() => this.usuario()?.rol === 'ADMINISTRADOR');
  readonly esMedico = computed(() => this.usuario()?.rol === 'MEDICO');
  readonly etiquetaPanel = computed(() => PANEL[this.usuario()?.rol ?? 'SECRETARIA']);
  readonly etiquetaRol = computed(() => ROL[this.usuario()?.rol ?? 'SECRETARIA']);
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
