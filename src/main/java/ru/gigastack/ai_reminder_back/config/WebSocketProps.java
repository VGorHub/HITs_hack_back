package ru.gigastack.ai_reminder_back.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "websocket")
@Data
public class WebSocketProps {
    private String endpoint;
    private Prefix prefix = new Prefix();

    @Data public static class Prefix {
        private String app;
        private String topic;
    }
}