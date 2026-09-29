/**
 * MediSistema - Javascript Interactivo de Demostración
 * Maneja aperturas de modales, cambio de estado de cola, filtros y simulaciones
 */

document.addEventListener('DOMContentLoaded', () => {
  // Manejo de Modales
  const modalTriggers = document.querySelectorAll('[data-modal-target]');
  const modalCloses = document.querySelectorAll('[data-modal-close]');

  modalTriggers.forEach(trigger => {
    trigger.addEventListener('click', (e) => {
      e.preventDefault();
      const targetId = trigger.getAttribute('data-modal-target');
      const targetModal = document.getElementById(targetId);
      if (targetModal) {
        targetModal.classList.add('show');
      }
    });
  });

  modalCloses.forEach(closeBtn => {
    closeBtn.addEventListener('click', () => {
      const parentModal = closeBtn.closest('.modal-backdrop');
      if (parentModal) {
        parentModal.classList.remove('show');
      }
    });
  });

  // Cerrar modal haciendo clic fuera de la tarjeta
  document.querySelectorAll('.modal-backdrop').forEach(modal => {
    modal.addEventListener('click', (e) => {
      if (e.target === modal) {
        modal.classList.remove('show');
      }
    });
  });

  // Simulador de Filtro Rápido en Tablas
  const searchInputs = document.querySelectorAll('.table-search-input');
  searchInputs.forEach(input => {
    input.addEventListener('keyup', () => {
      const filterValue = input.value.toLowerCase();
      const targetTableId = input.getAttribute('data-table-target');
      const table = document.getElementById(targetTableId);
      if (table) {
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
          const text = row.textContent.toLowerCase();
          row.style.display = text.includes(filterValue) ? '' : 'none';
        });
      }
    });
  });

  // Simulador de Botón "Solicitar Llamar Siguiente Paciente" (Médico)
  const btnLlamarSiguiente = document.getElementById('btn-solicitar-llamado');
  if (btnLlamarSiguiente) {
    btnLlamarSiguiente.addEventListener('click', () => {
      const banner = document.getElementById('banner-solicitud-llamado');
      if (banner) {
        banner.style.display = 'flex';
        banner.scrollIntoView({ behavior: 'smooth' });
      }
      alert('Solicitud enviada a la Secretaria. La recepcionista realizará la llamada del paciente.');
    });
  }

  // Simulador de Reemplazar Documento con Auditoría (Secretaría)
  const formReemplazarDoc = document.getElementById('form-reemplazar-doc');
  if (formReemplazarDoc) {
    formReemplazarDoc.addEventListener('submit', (e) => {
      e.preventDefault();
      const modal = document.getElementById('modal-reemplazar-documento');
      if (modal) modal.classList.remove('show');
      alert('Documento actualizado correctamente. Se ha registrado una entrada en la bitácora de auditoría (Regla N° 9 y N° 10).');
    });
  }
});
