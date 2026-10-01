package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.JornadaMedicaService;
import com.josbar.medisistemas.domain.dtos.jornada_medica.JornadaMedicaRequestDTO;
import com.josbar.medisistemas.domain.entities.DiaSemanaEntity;
import com.josbar.medisistemas.domain.entities.JornadaMedicaEntity;
import com.josbar.medisistemas.exceptions.BusinessRuleException;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.repositories.JornadaMedicaRepository;
import com.josbar.medisistemas.repositories.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JornadaMedicaServiceImpl implements JornadaMedicaService {

    private final JornadaMedicaRepository jornadaMedicaRepository;
    private final MedicoRepository medicoRepository;

    public JornadaMedicaServiceImpl(JornadaMedicaRepository jornadaMedicaRepository, MedicoRepository medicoRepository) {
        this.jornadaMedicaRepository = jornadaMedicaRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    @Transactional
    public JornadaMedicaEntity save(JornadaMedicaEntity entity) {
        CamposObligatorios.exigir(entity.getMedicoEntity().getId(), "médico");
        CamposObligatorios.exigir(entity.getDiaSemanaEntity().getId(), "día de la semana");
        CamposObligatorios.exigir(entity.getHoraInicio(), "hora de inicio");
        CamposObligatorios.exigir(entity.getHoraFin(), "hora de fin");
        CamposObligatorios.exigir(entity.getDuracionConsulta(), "duración de la consulta");
        validarHorario(entity);
        validarSinTraslape(entity);

        if (!medicoRepository.existsById(entity.getMedicoEntity().getId())) {
            throw new ResourceNotFoundException("No se encontró el médico con id " + entity.getMedicoEntity().getId());
        }

        return jornadaMedicaRepository.save(entity);
    }

    @Override
    public List<JornadaMedicaEntity> findByMedicoId(Integer idMedico) {
        return jornadaMedicaRepository.findByMedicoEntityId(idMedico);
    }

    @Override
    @Transactional
    public JornadaMedicaEntity modificar(Integer id, JornadaMedicaRequestDTO request) {
        JornadaMedicaEntity entity = jornadaMedicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la jornada con id " + id));

        if (request.getIdDiaSemana() != null) {
            DiaSemanaEntity dia = new DiaSemanaEntity();
            dia.setId(request.getIdDiaSemana());
            entity.setDiaSemanaEntity(dia);
        }
        if (request.getHoraInicio() != null) entity.setHoraInicio(request.getHoraInicio());
        if (request.getHoraFin() != null) entity.setHoraFin(request.getHoraFin());
        if (request.getDuracionConsulta() != null) entity.setDuracionConsulta(request.getDuracionConsulta());

        validarHorario(entity);
        validarSinTraslape(entity);
        return jornadaMedicaRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!jornadaMedicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la jornada con id " + id);
        }
        jornadaMedicaRepository.deleteById(id);
    }

    /**
     * Un médico no puede tener dos períodos que se traslapen el mismo día. Que uno termine justo cuando empieza
     * el otro (10:00-13:00 y 13:00-17:00) no es traslape.
     */
    private void validarSinTraslape(JornadaMedicaEntity nueva) {
        jornadaMedicaRepository
                .findByMedicoEntityIdAndDiaSemanaEntityId(nueva.getMedicoEntity().getId(), nueva.getDiaSemanaEntity().getId())
                .stream()
                .filter(existente -> !existente.getId().equals(nueva.getId()))
                .filter(existente -> nueva.getHoraInicio().isBefore(existente.getHoraFin())
                        && nueva.getHoraFin().isAfter(existente.getHoraInicio()))
                .findFirst()
                .ifPresent(existente -> {
                    throw new BusinessRuleException("El período " + nueva.getHoraInicio() + " - " + nueva.getHoraFin()
                            + " se traslapa con otro ya configurado para ese día (" + existente.getHoraInicio()
                            + " - " + existente.getHoraFin() + ").");
                });
    }

    private void validarHorario(JornadaMedicaEntity entity) {
        if (entity.getHoraInicio() != null && entity.getHoraFin() != null
                && !entity.getHoraInicio().isBefore(entity.getHoraFin())) {
            throw new BusinessRuleException("La hora de inicio debe ser anterior a la hora de finalización.");
        }
        if (entity.getDuracionConsulta() != null && entity.getDuracionConsulta() <= 0) {
            throw new BusinessRuleException("La duración de la consulta debe ser mayor a cero minutos.");
        }
    }
}
