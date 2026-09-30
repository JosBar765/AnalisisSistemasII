package com.josbar.medisistemas.config;

import com.josbar.medisistemas.domain.dtos.cita.EventoCitaDTO;
import com.josbar.medisistemas.security.JwtService;
import com.josbar.medisistemas.services.CitaEventoPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Canal de tiempo real de las citas. Solo comunica eventos; no ejecuta reglas de negocio.
 * <p>
 * El navegador no puede enviar cabeceras en el handshake y el token no debe viajar en la URL,
 * así que el primer mensaje del cliente debe ser el JWT. Hasta entonces la sesión no recibe nada.
 * Cada rol recibe únicamente los eventos que le corresponden: la secretaria todos los de citas
 * y el médico solo los de sus propias citas.
 */
@Component
public class CitaWebSocketHandler extends TextWebSocketHandler implements CitaEventoPublisher {

    private static final Logger log = LoggerFactory.getLogger(CitaWebSocketHandler.class);

    private static final String ATTR_ROL = "rol";
    private static final String ATTR_USUARIO_ID = "usuarioId";
    private static final String ROL_SECRETARIA = "SECRETARIA";
    private static final String ROL_MEDICO = "MEDICO";
    private static final long SEGUNDOS_PARA_AUTENTICAR = 10;

    private final JwtDecoder jwtDecoder;
    private final Set<WebSocketSession> sesionesAutenticadas = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService temporizador = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread hilo = new Thread(runnable, "ws-citas-autenticacion");
        hilo.setDaemon(true);
        return hilo;
    });

    public CitaWebSocketHandler(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        temporizador.schedule(() -> cerrarSiNoSeAutentico(session), SEGUNDOS_PARA_AUTENTICAR, TimeUnit.SECONDS);
    }

    private void cerrarSiNoSeAutentico(WebSocketSession session) {
        if (session.isOpen() && !sesionesAutenticadas.contains(session)) {
            try {
                session.close(CloseStatus.POLICY_VIOLATION.withReason("Autenticación requerida"));
            } catch (IOException e) {
                log.warn("No se pudo cerrar la sesión {} sin autenticar: {}", session.getId(), e.getMessage());
            }
        }
    }

    @PreDestroy
    void detenerTemporizador() {
        temporizador.shutdownNow();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            Jwt jwt = jwtDecoder.decode(message.getPayload().trim());
            session.getAttributes().put(ATTR_ROL, jwt.getClaimAsString(JwtService.CLAIM_ROL));
            session.getAttributes().put(ATTR_USUARIO_ID, Integer.valueOf(jwt.getSubject()));
            sesionesAutenticadas.add(session);
            enviar(session, "{\"tipo\":\"AUTH_OK\"}");
        } catch (JwtException | NumberFormatException e) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Token inválido"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sesionesAutenticadas.remove(session);
    }

    @Override
    public void publicar(EventoCitaDTO evento) {
        String json = String.format("{\"tipo\":\"%s\",\"citaId\":%d,\"medicoId\":%d}",
                evento.tipo(), evento.citaId(), evento.medicoId());
        Runnable difusion = () -> sesionesAutenticadas.stream()
                .filter(session -> puedeRecibir(session, evento))
                .forEach(session -> enviar(session, json));

        // El cliente consulta por REST al recibir el evento: debe hacerlo cuando ya se confirmó la transacción.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    difusion.run();
                }
            });
        } else {
            difusion.run();
        }
    }

    private boolean puedeRecibir(WebSocketSession session, EventoCitaDTO evento) {
        Object rol = session.getAttributes().get(ATTR_ROL);
        if (ROL_SECRETARIA.equals(rol)) {
            return true;
        }
        return ROL_MEDICO.equals(rol) && evento.medicoId().equals(session.getAttributes().get(ATTR_USUARIO_ID));
    }

    private void enviar(WebSocketSession session, String json) {
        try {
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (IOException e) {
            log.warn("No se pudo enviar el evento a la sesión {}: {}", session.getId(), e.getMessage());
        }
    }
}
