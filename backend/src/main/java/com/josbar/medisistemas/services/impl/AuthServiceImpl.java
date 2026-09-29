package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.AuthService;
import com.josbar.medisistemas.domain.dtos.auth.AuthResponseDTO;
import com.josbar.medisistemas.domain.dtos.auth.LoginRequestDTO;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.exceptions.InvalidCredentialsException;
import com.josbar.medisistemas.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Nota: MediSistema todavía no implementa JWT (ver .agents/reglas_despliegue.md sección 9).
 * Esta implementación valida credenciales reales contra la base de datos, pero el "token"
 * devuelto es un identificador opaco sin firma ni expiración; el Frontend, además, ignora
 * la respuesta y redirige directo al dashboard (bypass intencional del login solicitado
 * para esta fase). Debe sustituirse por un JWT firmado cuando se implemente esa fase.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDTO autenticar(LoginRequestDTO loginRequest) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(loginRequest.getCorreo())
                .orElseThrow(() -> new InvalidCredentialsException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(loginRequest.getContrasenia(), usuario.getContrasenia())) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos.");
        }
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new InvalidCredentialsException("El usuario se encuentra inactivo.");
        }

        return AuthResponseDTO.builder()
                .token(UUID.randomUUID().toString())
                .build();
    }
}
