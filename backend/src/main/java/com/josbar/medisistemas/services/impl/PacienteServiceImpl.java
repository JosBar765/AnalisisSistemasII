package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.PacienteService;
import com.josbar.medisistemas.domain.dtos.paciente.PacienteRequestDTO;
import com.josbar.medisistemas.domain.entities.PacienteEntity;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.PacienteMapper;
import com.josbar.medisistemas.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    public PacienteServiceImpl(PacienteRepository pacienteRepository, PacienteMapper pacienteMapper) {
        this.pacienteRepository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
    }

    @Override
    @Transactional
    public PacienteEntity save(PacienteEntity entity) {
        if (entity.getEstado() == null) {
            entity.setEstado(true);
        }
        return pacienteRepository.save(entity);
    }

    @Override
    public List<PacienteEntity> findAll() {
        return (List<PacienteEntity>) pacienteRepository.findAll();
    }

    @Override
    public PacienteEntity findById(Integer id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con id " + id));
    }

    @Override
    public PacienteEntity findByDpi(String dpi) {
        return pacienteRepository.findByDpi(dpi)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un paciente con DPI " + dpi));
    }

    @Override
    @Transactional
    public PacienteEntity modificar(Integer id, PacienteRequestDTO request) {
        PacienteEntity entity = findById(id);
        pacienteMapper.updateEntity(request, entity);
        return pacienteRepository.save(entity);
    }

    @Override
    @Transactional
    public PacienteEntity cambiarEstado(Integer id, Boolean estado) {
        PacienteEntity entity = findById(id);
        entity.setEstado(estado);
        return pacienteRepository.save(entity);
    }
}
