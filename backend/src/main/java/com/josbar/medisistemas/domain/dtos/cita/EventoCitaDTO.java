package com.josbar.medisistemas.domain.dtos.cita;

/**
 * Evento de tiempo real. Solo indica qué cita cambió; el detalle se obtiene por REST.
 */
public record EventoCitaDTO(TipoEventoCita tipo, Integer citaId, Integer medicoId) {
}
