import { Component, input } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SignosVitales } from '../../models/consulta.model';

/** Grupo de formulario de los cinco signos vitales (peso en kg y altura en cm, como en el modelo de datos). */
export function crearGrupoSignosVitales(fb: FormBuilder, valor?: SignosVitales | null): FormGroup {
  return fb.nonNullable.group({
    peso: [valor?.peso ?? (null as number | null), [Validators.required, Validators.min(0.1), Validators.max(500)]],
    altura: [valor?.altura ?? (null as number | null), [Validators.required, Validators.min(20), Validators.max(260)]],
    presionSistolica: [valor?.presionSistolica ?? (null as number | null), [Validators.required, Validators.min(30), Validators.max(300)]],
    presionDiastolica: [valor?.presionDiastolica ?? (null as number | null), [Validators.required, Validators.min(20), Validators.max(200)]],
    temperatura: [valor?.temperatura ?? (null as number | null), [Validators.required, Validators.min(25), Validators.max(45)]],
  });
}

@Component({
  selector: 'app-signos-vitales-campos',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div [formGroup]="grupo()" class="form-row">
      <div class="form-group">
        <label class="form-label">Peso (kg)</label>
        <input type="number" step="0.1" class="form-control" formControlName="peso" />
      </div>
      <div class="form-group">
        <label class="form-label">Altura (cm)</label>
        <input type="number" step="0.1" class="form-control" formControlName="altura" />
      </div>
      <div class="form-group">
        <label class="form-label">Presión Sistólica (mmHg)</label>
        <input type="number" class="form-control" formControlName="presionSistolica" />
      </div>
      <div class="form-group">
        <label class="form-label">Presión Diastólica (mmHg)</label>
        <input type="number" class="form-control" formControlName="presionDiastolica" />
      </div>
      <div class="form-group">
        <label class="form-label">Temperatura (°C)</label>
        <input type="number" step="0.1" class="form-control" formControlName="temperatura" />
      </div>
    </div>
  `,
})
export class SignosVitalesCamposComponent {
  readonly grupo = input.required<FormGroup>();
}
