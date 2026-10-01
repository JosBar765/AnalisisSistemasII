package com.josbar.medisistemas.security;

import com.josbar.medisistemas.repositories.UsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Un JWT es válido mientras no venza, pero la sesión solo lo es mientras el usuario siga existiendo y activo.
 * Se consulta en cada petición REST y al autenticar el WebSocket, así que desactivar un usuario invalida
 * sus tokens al instante.
 */
@Component
public class SesionUsuarioValidator {

    private final UsuarioRepository usuarioRepository;

    public SesionUsuarioValidator(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean estaActivo(Integer idUsuario) {
        return usuarioRepository.existsByIdAndEstadoTrue(idUsuario);
    }
}
