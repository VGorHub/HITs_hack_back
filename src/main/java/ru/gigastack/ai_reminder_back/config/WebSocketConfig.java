package ru.gigastack.ai_reminder_back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketProps props;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry cfg) {
        cfg.enableSimpleBroker(props.getPrefix().getTopic());   // /topic/**
        cfg.setApplicationDestinationPrefixes(props.getPrefix().getApp()); // /app/**
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry reg) {
        reg.addEndpoint(props.getEndpoint()).setAllowedOriginPatterns("*").withSockJS();
    }
}