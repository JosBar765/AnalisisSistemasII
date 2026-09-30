package com.josbar.medisistemas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CitaWebSocketHandler citaWebSocketHandler;
    private final String[] allowedOrigins;

    public WebSocketConfig(CitaWebSocketHandler citaWebSocketHandler,
                           @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.citaWebSocketHandler = citaWebSocketHandler;
        this.allowedOrigins = allowedOrigins.split(",");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(citaWebSocketHandler, "/ws/citas").setAllowedOrigins(allowedOrigins);
    }
}
