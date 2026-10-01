package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.AuthService;
import com.josbar.medisistemas.domain.dtos.auth.AuthResponseDTO;
import com.josbar.medisistemas.domain.dtos.auth.LoginRequestDTO;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.exceptions.InvalidCredentialsException;
import com.josbar.medisistemas.repositories.UsuarioRepository;
import com.josbar.medisistemas.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.josbar.medisistemas.utils.Texto;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDTO autenticar(LoginRequestDTO loginRequest) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(Texto.minusculas(loginRequest.getCorreo()))
                .orElseThrow(() -> new InvalidCredentialsException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(loginRequest.getContrasenia(), usuario.getContrasenia())) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos.");
        }
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new InvalidCredentialsException("El usuario se encuentra inactivo.");
        }

        return AuthResponseDTO.builder()
                .token(jwtService.generarToken(usuario))
                .build();
    }
}
