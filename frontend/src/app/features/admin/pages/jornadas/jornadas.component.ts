import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { JornadaService } from '../../services/jornada.service';
import { MedicoService } from '../../services/medico.service';
import { CatalogoService } from '../../services/catalogo.service';
import { JornadaMedica } from '../../models/jornada.model';
import { Medico } from '../../models/medico.model';
import { Catalogo } from '../../models/catalogo.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-jornadas',
  standalone: true,
  imports: [ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './jornadas.component.html',
})
export class JornadasComponent implements OnInit {
  private readonly jornadaService = inject(JornadaService);
  private readonly medicoService = inject(MedicoService);
  private readonly catalogoService = inject(CatalogoService);
  private readonly fb = inject(FormBuilder);

  readonly medicos = signal<Medico[]>([]);
  readonly diasSemana = signal<Catalogo[]>([]);
  readonly jornadas = signal<JornadaMedica[]>([]);
  readonly idMedicoSeleccionado = signal<number>(0);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly modalAbierto = signal(false);
  readonly jornadaEditando = signal<JornadaMedica | null>(null);

  readonly form = this.fb.nonNullable.group({
    idDiaSemana: [0, [Validators.required, Validators.min(1)]],
    horaInicio: ['', Validators.required],
    horaFin: ['', Validators.required],
    duracionConsulta: [30, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.medicoService.listar().subscribe({ next: (data) => this.medicos.set(data) });
    this.catalogoService.listarDiasSemana().subscribe({ next: (data) => this.diasSemana.set(data) });
  }

  seleccionarMedico(idMedico: number): void {
    this.idMedicoSeleccionado.set(idMedico);
    if (!idMedico) {
      this.jornadas.set([]);
      return;
    }
    this.cargarJornadas();
  }

  cargarJornadas(): void {
    const idMedico = this.idMedicoSeleccionado();
    if (!idMedico) return;

    this.cargando.set(true);
    this.error.set(null);
    this.jornadaService.listarPorMedico(idMedico).subscribe({
      next: (data) => {
        this.jornadas.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las jornadas del médico seleccionado.');
        this.cargando.set(false);
      },
    });
  }

  abrirNueva(): void {
    if (!this.idMedicoSeleccionado()) {
      this.error.set('Seleccione un médico antes de configurar un período.');
      return;
    }
    this.jornadaEditando.set(null);
    this.form.reset({ idDiaSemana: 0, horaInicio: '', horaFin: '', duracionConsulta: 30 });
    this.modalAbierto.set(true);
  }

  abrirEditar(jornada: JornadaMedica): void {
    this.jornadaEditando.set(jornada);
    this.form.reset({
      idDiaSemana: jornada.diaSemanaResponseDTO.id,
      horaInicio: jornada.horaInicio.slice(0, 5),
      horaFin: jornada.horaFin.slice(0, 5),
      duracionConsulta: jornada.duracionConsulta,
    });
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
    const editando = this.jornadaEditando();

    const peticion = editando
      ? this.jornadaService.modificar(editando.id, valor)
      : this.jornadaService.registrar({ idMedico: this.idMedicoSeleccionado(), ...valor });

    peticion.subscribe({
      next: () => {
        this.cerrarModal();
        this.cargarJornadas();
      },
      error: () => this.error.set('No se pudo guardar la jornada. Verifique que la hora de inicio sea anterior a la de fin.'),
    });
  }

  eliminar(jornada: JornadaMedica): void {
    this.jornadaService.eliminar(jornada.id).subscribe({
      next: () => this.cargarJornadas(),
      error: () => this.error.set('No se pudo eliminar la jornada.'),
    });
  }
}
