package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.auth.AuthResponseDTO;
import com.josbar.medisistemas.domain.dtos.auth.LoginRequestDTO;

public interface AuthService {
    AuthResponseDTO autenticar(LoginRequestDTO loginRequest);
}
