package ru.gigastack.ai_reminder_back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.gigastack.ai_reminder_back.security.ws.JwtHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketProps          props;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry cfg) {
        cfg.enableSimpleBroker("/queue", "/topic");  // in-memory broker
        cfg.setUserDestinationPrefix("/user");       // <– ключевая строка!
        cfg.setApplicationDestinationPrefixes(props.getPrefix().getApp()); // /app/**
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry reg) {
        reg.addEndpoint(props.getEndpoint())
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor) // <– привязали JWT к сессии
                .withSockJS();
    }
}