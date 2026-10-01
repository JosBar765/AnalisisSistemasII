import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConsultaService } from '../../services/consulta.service';
import { CitaConsulta, SignosVitales } from '../../models/consulta.model';
import {
  SignosVitalesCamposComponent,
  crearGrupoSignosVitales,
} from '../../components/signos-vitales-campos/signos-vitales-campos.component';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { EdadPipe } from '../../../../shared/pipes/edad.pipe';
import { Hora12Pipe } from '../../../../shared/pipes/hora12.pipe';
import { NombreCompletoPipe } from '../../../../shared/pipes/nombre-completo.pipe';
import { NotificationService } from '../../../../core/services/notification.service';
import { mensajeDeError } from '../../../../shared/utils/mensaje-error';

/** Atención clínica de una cita (UC-MED-003): al finalizar, la cita pasa a "Atendido". */
@Component({
  selector: 'app-consulta-nueva',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    LoadingSpinnerComponent,
    SignosVitalesCamposComponent,
    EdadPipe,
    Hora12Pipe,
    NombreCompletoPipe,
  ],
  templateUrl: './nueva.component.html',
})
export class NuevaConsultaComponent implements OnInit {
  private readonly notificacion = inject(NotificationService);
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly consultaService = inject(ConsultaService);

  readonly cita = signal<CitaConsulta | null>(null);
  readonly cargando = signal(true);
  readonly guardando = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    motivoConsulta: ['', Validators.required],
    signos: crearGrupoSignosVitales(this.fb),
    diagnostico: ['', Validators.required],
    tratamiento: ['', Validators.required],
    observaciones: [''],
  });

  ngOnInit(): void {
    const idCita = Number(this.route.snapshot.paramMap.get('idCita'));
    this.consultaService.obtenerCita(idCita).subscribe({
      next: (cita) => {
        this.cita.set(cita);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se encontró la cita o no pertenece a su agenda.');
        this.cargando.set(false);
      },
    });
  }

  finalizar(): void {
    const cita = this.cita();
    if (!cita || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valor = this.form.getRawValue();
    this.guardando.set(true);
    this.error.set(null);
    this.consultaService
      .registrar({
        idCita: cita.id,
        motivoConsulta: valor.motivoConsulta.trim(),
        diagnostico: valor.diagnostico.trim(),
        tratamiento: valor.tratamiento.trim(),
        observaciones: valor.observaciones.trim() || undefined,
        signosVitalesRequestDTO: valor.signos as unknown as SignosVitales,
      })
      .subscribe({
        next: () => this.router.navigate(['/agenda']),
        error: (err: HttpErrorResponse) => {
          this.notificacion.error(mensajeDeError(err, 'No se pudo guardar la consulta.'));
          this.guardando.set(false);
        },
      });
  }
}
