package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.CierreSesionPublisher;
import com.josbar.medisistemas.services.UsuarioService;
import com.josbar.medisistemas.domain.dtos.usuario.UsuarioRequestDTO;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.exceptions.BusinessRuleException;
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

    private static final int TAMANIO_MAXIMO_CONTRASENIA = 72; // límite de BCrypt

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final CierreSesionPublisher cierreSesionPublisher;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder,
                              CierreSesionPublisher cierreSesionPublisher) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.cierreSesionPublisher = cierreSesionPublisher;
    }

    @Override
    @Transactional
    public UsuarioEntity save(UsuarioEntity entity) {
        CamposObligatorios.exigir(entity.getRolEntity().getId(), "rol");
        CamposObligatorios.exigir(entity.getPrimerNombre(), "primer nombre");
        CamposObligatorios.exigir(entity.getPrimerApellido(), "primer apellido");
        CamposObligatorios.exigir(entity.getSegundoApellido(), "segundo apellido");
        CamposObligatorios.exigir(entity.getCorreo(), "correo electrónico");
        CamposObligatorios.exigir(entity.getTelefono(), "teléfono");
        CamposObligatorios.exigir(entity.getContrasenia(), "contraseña");
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
        return guardarYCerrarSesionesSiInactivo(entity);
    }

    @Override
    @Transactional
    public UsuarioEntity cambiarEstado(Integer id, Boolean estado) {
        UsuarioEntity entity = findById(id);
        entity.setEstado(estado);
        return guardarYCerrarSesionesSiInactivo(entity);
    }

    @Override
    @Transactional
    public void cambiarContrasenia(Integer id, String nuevaContrasenia) {
        CamposObligatorios.exigir(nuevaContrasenia, "contraseña");
        if (nuevaContrasenia.length() > TAMANIO_MAXIMO_CONTRASENIA) {
            throw new BusinessRuleException("La contraseña no puede superar " + TAMANIO_MAXIMO_CONTRASENIA + " caracteres.");
        }
        UsuarioEntity entity = findById(id);
        entity.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        usuarioRepository.save(entity);
    }

    /** Un usuario inactivo pierde su sesión: el JWT deja de aceptarse y se cierra su conexión en tiempo real. */
    private UsuarioEntity guardarYCerrarSesionesSiInactivo(UsuarioEntity entity) {
        UsuarioEntity guardado = usuarioRepository.save(entity);
        if (Boolean.FALSE.equals(guardado.getEstado())) {
            cierreSesionPublisher.cerrarSesionesDe(guardado.getId());
        }
        return guardado;
    }
}
