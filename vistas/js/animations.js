/**
 * MediSistema - Animaciones y Efectos Visuales
 */

document.addEventListener('DOMContentLoaded', () => {
  // Animación de entrada suave para tarjetas
  const cards = document.querySelectorAll('.card, .metric-card');
  cards.forEach((card, index) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(12px)';
    card.style.transition = 'opacity 0.4s ease, transform 0.4s ease';

    setTimeout(() => {
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, 60 * index);
  });

  // Animación de pulso para llamadas activas en recepcion / llamador
  const activeCallPills = document.querySelectorAll('.pulse-active');
  activeCallPills.forEach(pill => {
    setInterval(() => {
      pill.style.boxShadow = pill.style.boxShadow ? '' : '0 0 0 6px rgba(8, 131, 149, 0.25)';
    }, 800);
  });
});
