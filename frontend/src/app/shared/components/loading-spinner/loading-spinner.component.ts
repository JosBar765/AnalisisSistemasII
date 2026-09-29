import { Component } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  template: `<div class="loading-spinner" role="status" aria-label="Cargando"></div>`,
  styles: [`
    .loading-spinner {
      width: 28px;
      height: 28px;
      margin: 24px auto;
      border: 3px solid var(--color-border-light);
      border-top-color: var(--color-primary);
      border-radius: 50%;
      animation: spin 0.7s linear infinite;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `],
})
export class LoadingSpinnerComponent {}
