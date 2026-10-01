package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.MedicoService;
import com.josbar.medisistemas.domain.dtos.medico.MedicoRequestDTO;
import com.josbar.medisistemas.domain.entities.EspecialidadEntity;
import com.josbar.medisistemas.domain.entities.MedicoEntity;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.exceptions.BusinessRuleException;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.repositories.MedicoRepository;
import com.josbar.medisistemas.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.josbar.medisistemas.security.Roles;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;

    public MedicoServiceImpl(MedicoRepository medicoRepository, UsuarioRepository usuarioRepository) {
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public MedicoEntity save(MedicoEntity entity) {
        CamposObligatorios.exigir(entity.getUsuarioEntity().getId(), "usuario");
        CamposObligatorios.exigir(entity.getEspecialidad().getId(), "especialidad");
        CamposObligatorios.exigir(entity.getColegiado(), "número de colegiado");
        UsuarioEntity usuario = usuarioRepository.findById(entity.getUsuarioEntity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario asociado al médico."));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new BusinessRuleException("El médico debe estar asociado a un usuario activo del sistema.");
        }
        if (!Roles.MEDICO.equals(usuario.getRolEntity().getRol())) {
            throw new BusinessRuleException("El usuario debe tener el rol MEDICO para registrarse como médico (rol actual: "
                    + usuario.getRolEntity().getRol() + ").");
        }

        entity.setUsuarioEntity(usuario);
        return medicoRepository.save(entity);
    }

    @Override
    public List<MedicoEntity> findAll() {
        return (List<MedicoEntity>) medicoRepository.findAll();
    }

    @Override
    public MedicoEntity findById(Integer id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el médico con id " + id));
    }

    @Override
    @Transactional
    public MedicoEntity modificar(Integer id, MedicoRequestDTO request) {
        MedicoEntity entity = findById(id);

        if (request.getColegiado() != null) {
            entity.setColegiado(request.getColegiado());
        }
        if (request.getIdEspecialidad() != null) {
            EspecialidadEntity especialidad = new EspecialidadEntity();
            especialidad.setId(request.getIdEspecialidad());
            entity.setEspecialidad(especialidad);
        }
        return medicoRepository.save(entity);
    }
}
