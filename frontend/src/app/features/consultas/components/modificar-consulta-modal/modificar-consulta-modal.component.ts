import { Component, OnInit, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CatalogoConsulta, Consulta, ModificarConsultaRequest, SignosVitales } from '../../models/consulta.model';
import {
  SignosVitalesCamposComponent,
  crearGrupoSignosVitales,
} from '../signos-vitales-campos/signos-vitales-campos.component';

/** Modal de corrección de una consulta finalizada. No guarda: emite la solicitud. */
@Component({
  selector: 'app-modificar-consulta-modal',
  standalone: true,
  imports: [ReactiveFormsModule, SignosVitalesCamposComponent],
  templateUrl: './modificar-consulta-modal.component.html',
})
export class ModificarConsultaModalComponent implements OnInit {
  private readonly fb = inject(FormBuilder);

  readonly consulta = input.required<Consulta>();
  readonly motivos = input.required<CatalogoConsulta[]>();
  readonly error = input<string | null>(null);
  readonly guardando = input(false);

  readonly guardar = output<ModificarConsultaRequest>();
  readonly cerrar = output<void>();

  readonly form = this.fb.nonNullable.group({
    motivoConsulta: ['', Validators.required],
    signos: crearGrupoSignosVitales(this.fb),
    diagnostico: ['', Validators.required],
    tratamiento: ['', Validators.required],
    observaciones: [''],
    idMotivoModificacionConsulta: [null as number | null, Validators.required],
  });

  ngOnInit(): void {
    const consulta = this.consulta();
    this.form.patchValue({
      motivoConsulta: consulta.motivoConsulta,
      diagnostico: consulta.diagnostico,
      tratamiento: consulta.tratamiento,
      observaciones: consulta.observaciones ?? '',
    });
    this.form.controls.signos.patchValue(consulta.signosVitalesRequestDTO ?? {});
  }

  enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valor = this.form.getRawValue();
    this.guardar.emit({
      idMotivoModificacionConsulta: Number(valor.idMotivoModificacionConsulta),
      motivoConsulta: valor.motivoConsulta.trim(),
      diagnostico: valor.diagnostico.trim(),
      tratamiento: valor.tratamiento.trim(),
      observaciones: valor.observaciones.trim(),
      signosVitalesRequestDTO: valor.signos as unknown as SignosVitales,
    });
  }
}
