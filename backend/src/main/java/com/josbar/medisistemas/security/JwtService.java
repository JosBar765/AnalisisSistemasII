package com.josbar.medisistemas.security;

import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Genera el JWT firmado (HS256). El token lleva el id del usuario (sub), su rol
 * (claim "rol", usado por Spring Security para autorizar) y su nombre para mostrarlo en Angular.
 */
@Service
public class JwtService {

    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_NOMBRE = "nombre";

    private final JwtEncoder jwtEncoder;
    private final Duration expiracion;

    public JwtService(JwtEncoder jwtEncoder, @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.expiracion = Duration.ofMinutes(expirationMinutes);
    }

    public String generarToken(UsuarioEntity usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(usuario.getId()))
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expiracion))
                .claim(CLAIM_ROL, usuario.getRolEntity().getRol())
                .claim(CLAIM_NOMBRE, usuario.getPrimerNombre() + " " + usuario.getPrimerApellido())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
