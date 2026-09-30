package com.josbar.medisistemas.config;

import com.josbar.medisistemas.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Autenticación stateless con JWT. El rol viaja en el claim "rol" y se traduce a
 * la autoridad ROLE_<rol>. Aquí solo se protege lo que ya está definido en el análisis
 * (administración y secretaria); el resto exige estar autenticado hasta que cada módulo
 * defina sus reglas por rol.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMINISTRADOR = "ADMINISTRADOR";
    private static final String SECRETARIA = "SECRETARIA";
    private static final String MEDICO = "MEDICO";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
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
                        // Consulta: secretaria y médico. Registro y modificación: solo secretaria.
                        .requestMatchers(HttpMethod.GET, "/pacientes/**", "/citas/**", "/documentos/**",
                                "/auditorias/documentos/**").hasAnyRole(SECRETARIA, MEDICO)
                        .requestMatchers("/pacientes/**", "/citas/**", "/documentos/**").hasRole(SECRETARIA)
                        // La secretaria necesita ver los médicos para programar citas.
                        .requestMatchers(HttpMethod.GET, "/medicos/**").hasAnyRole(ADMINISTRADOR, SECRETARIA)
                        .requestMatchers("/usuarios/**", "/medicos/**", "/especialidades/**",
                                "/jornadas/**", "/dashboard/**", "/categorias-documento/**")
                        .hasRole(ADMINISTRADOR)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName(JwtService.CLAIM_ROL);
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
