import { Injectable, effect, inject, signal, untracked } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

/** Datos del perfil profesional del médico autenticado que no viajan en el JWT (por ahora, su especialidad). */
@Injectable({ providedIn: 'root' })
export class PerfilMedicoService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  readonly especialidad = signal<string | null>(null);

  constructor() {
    effect(() => {
      const esMedico = this.auth.usuario()?.rol === 'MEDICO';
      untracked(() => (esMedico ? this.cargar() : this.especialidad.set(null)));
    });
  }

  private cargar(): void {
    this.http
      .get<{ especialidadResponseDTO: { nombre: string } }>(`${environment.apiUrl}/medicos/me`)
      .subscribe({
        next: (medico) => this.especialidad.set(medico.especialidadResponseDTO.nombre),
        error: () => this.especialidad.set(null),
      });
  }
}
