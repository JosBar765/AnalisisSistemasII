package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.DashboardService;
import com.josbar.medisistemas.domain.dtos.dashboard.DashboardResponseDTO;
import com.josbar.medisistemas.repositories.CitaRepository;
import com.josbar.medisistemas.repositories.ConsultaRepository;
import com.josbar.medisistemas.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import com.josbar.medisistemas.utils.Texto;
import com.josbar.medisistemas.domain.dtos.dashboard.ConsultaPorMedicoDTO;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;
    private final CitaRepository citaRepository;

    public DashboardServiceImpl(PacienteRepository pacienteRepository, ConsultaRepository consultaRepository, CitaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.consultaRepository = consultaRepository;
        this.citaRepository = citaRepository;
    }

    @Override
    public DashboardResponseDTO obtenerMetricas() {
        return DashboardResponseDTO.builder()
                .pacientesRegistrados(pacienteRepository.count())
                .consultasRealizadas(consultaRepository.count())
                .citasCanceladas(citaRepository.totalCancelaciones())
                .consultasPorMedico(consultaRepository.obtenerConsultasPorMedico().stream()
                        .map(c -> new ConsultaPorMedicoDTO(c.getIdMedico(), Texto.titulo(c.getNombreMedico()), c.getCantidad()))
                        .toList())
                .build();
    }
}
