package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.cita.EventoCitaDTO;

public interface CitaEventoPublisher {
    void publicar(EventoCitaDTO evento);
}
