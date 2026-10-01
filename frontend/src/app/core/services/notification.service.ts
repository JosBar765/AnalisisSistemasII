import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

/**
 * Avisos emergentes (SweetAlert2) que se muestran encima de cualquier formulario o modal abierto, para que
 * el usuario lea la causa de un error sin perder lo que estaba llenando.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  error(mensaje: string, titulo = 'No se pudo completar la acción'): void {
    void Swal.fire({
      icon: 'error',
      title: titulo,
      text: mensaje,
      confirmButtonText: 'Entendido',
      buttonsStyling: false,
      customClass: { confirmButton: 'btn btn-primary' },
    });
  }
}
