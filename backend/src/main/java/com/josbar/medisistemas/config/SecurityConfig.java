package com.josbar.medisistemas.config;

import com.josbar.medisistemas.security.JwtService;
import com.josbar.medisistemas.security.SesionUsuarioValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Autenticación stateless con JWT. El rol viaja en el claim "rol" y se traduce a
 * la autoridad ROLE_<rol>. Aquí solo se protege lo que ya está definido en el análisis
 * (administración, secretaria y médico); el resto exige estar autenticado.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMINISTRADOR = "ADMINISTRADOR";
    private static final String SECRETARIA = "SECRETARIA";
    private static final String MEDICO = "MEDICO";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource,
                                                   SesionUsuarioValidator sesionValidator) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        // El handshake es público; el WebSocket valida el JWT en su primer mensaje.
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/citas/*/llamado").hasRole(MEDICO)
                        // El médico solo ve su propia agenda; la información clínica es solo del médico.
                        .requestMatchers(HttpMethod.GET, "/citas/mis-citas/**").hasRole(MEDICO)
                        .requestMatchers("/consultas/**", "/expedientes/**", "/auditorias/consultas/**").hasRole(MEDICO)
                        // Consulta: secretaria y médico. Registro y modificación: solo secretaria.
                        .requestMatchers(HttpMethod.GET, "/pacientes/**", "/documentos/**",
                                "/auditorias/documentos/**").hasAnyRole(SECRETARIA, MEDICO)
                        .requestMatchers("/pacientes/**", "/citas/**", "/documentos/**").hasRole(SECRETARIA)
                        // La secretaria necesita ver los médicos para programar citas.
                        .requestMatchers(HttpMethod.GET, "/medicos/me").hasRole(MEDICO)
                        .requestMatchers(HttpMethod.GET, "/medicos/**").hasAnyRole(ADMINISTRADOR, SECRETARIA)
                        .requestMatchers("/usuarios/**", "/medicos/**", "/especialidades/**",
                                "/jornadas/**", "/dashboard/**", "/categorias-documento/**")
                        .hasRole(ADMINISTRADOR)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(sesionValidator))))
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Rechaza (401) el token de un usuario que fue desactivado o eliminado, aunque aún no haya vencido. */
    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(SesionUsuarioValidator sesionValidator) {
        JwtAuthenticationConverter convertidor = convertidorDeRoles();
        return jwt -> {
            if (!sesionValidator.estaActivo(Integer.valueOf(jwt.getSubject()))) {
                throw new InvalidBearerTokenException("La sesión ya no es válida: el usuario está inactivo.");
            }
            return convertidor.convert(jwt);
        };
    }

    private JwtAuthenticationConverter convertidorDeRoles() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName(JwtService.CLAIM_ROL);
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
