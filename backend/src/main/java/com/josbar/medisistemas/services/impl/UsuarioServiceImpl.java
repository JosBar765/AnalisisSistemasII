package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.UsuarioService;
import com.josbar.medisistemas.domain.dtos.usuario.UsuarioRequestDTO;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.UsuarioMapper;
import com.josbar.medisistemas.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioEntity save(UsuarioEntity entity) {
        entity.setFechaCreacion(LocalDateTime.now());
        if (entity.getEstado() == null) {
            entity.setEstado(true);
        }
        entity.setContrasenia(passwordEncoder.encode(entity.getContrasenia()));
        return usuarioRepository.save(entity);
    }

    @Override
    public List<UsuarioEntity> findAll() {
        return (List<UsuarioEntity>) usuarioRepository.findAll();
    }

    @Override
    public UsuarioEntity findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con id " + id));
    }

    @Override
    @Transactional
    public UsuarioEntity modificar(Integer id, UsuarioRequestDTO request) {
        UsuarioEntity entity = findById(id);
        usuarioMapper.updateEntity(request, entity);
        return usuarioRepository.save(entity);
    }

    @Override
    @Transactional
    public UsuarioEntity cambiarEstado(Integer id, Boolean estado) {
        UsuarioEntity entity = findById(id);
        entity.setEstado(estado);
        return usuarioRepository.save(entity);
    }

    @Override
    @Transactional
    public void cambiarContrasenia(Integer id, String nuevaContrasenia) {
        UsuarioEntity entity = findById(id);
        entity.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        usuarioRepository.save(entity);
    }
}
