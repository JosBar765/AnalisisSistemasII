package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.EspecialidadService;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoRequestDTO;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.domain.entities.EspecialidadEntity;
import com.josbar.medisistemas.exceptions.BusinessRuleException;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.CatalogoMapper;
import com.josbar.medisistemas.repositories.EspecialidadRepository;
import com.josbar.medisistemas.repositories.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;
    private final CatalogoMapper catalogoMapper;

    public EspecialidadServiceImpl(EspecialidadRepository especialidadRepository, MedicoRepository medicoRepository, CatalogoMapper catalogoMapper) {
        this.especialidadRepository = especialidadRepository;
        this.medicoRepository = medicoRepository;
        this.catalogoMapper = catalogoMapper;
    }

    @Override
    @Transactional
    public CatalogoResponseDTO crear(CatalogoRequestDTO request) {
        EspecialidadEntity entity = EspecialidadEntity.builder()
                .especialidad(request.getNombre())
                .build();
        return catalogoMapper.toResponse(especialidadRepository.save(entity));
    }

    @Override
    @Transactional
    public CatalogoResponseDTO editar(Integer id, CatalogoRequestDTO request) {
        EspecialidadEntity entity = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la especialidad con id " + id));
        entity.setEspecialidad(request.getNombre());
        return catalogoMapper.toResponse(especialidadRepository.save(entity));
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        EspecialidadEntity entity = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la especialidad con id " + id));

        if (medicoRepository.existsByEspecialidadId(id)) {
            throw new BusinessRuleException("No se puede eliminar la especialidad porque tiene médicos asociados.");
        }
        especialidadRepository.delete(entity);
    }
}
