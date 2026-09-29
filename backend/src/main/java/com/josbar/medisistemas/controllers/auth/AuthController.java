package com.josbar.medisistemas.controllers.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.josbar.medisistemas.domain.dtos.auth.LoginRequestDTO;
import com.josbar.medisistemas.domain.dtos.auth.AuthResponseDTO;
import com.josbar.medisistemas.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> autenticarUsuario(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO tokenResponse = authService.autenticar(loginRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
}