package ru.gigastack.ai_reminder_back.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class SocketIOConfig {

    @Value("${socketio.port:9092}")
    private int port;

    @Value("${socketio.keystore:keystore.p12}")
    private String keyStorePath;

    @Value("${socketio.keystore-password:changeit}")
    private String keyStorePassword;

    // ✅ создаём и настраиваем ObjectMapper прямо тут
    @Bean
    public ObjectMapper socketIOMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean(destroyMethod = "stop")
    public SocketIOServer socketIOServer(ObjectMapper socketIOMapper) throws IOException {
        var cfg = new com.corundumstudio.socketio.Configuration();
        cfg.setPort(port);
        cfg.setOrigin("*");

        // ✅ передаём ObjectMapper с поддержкой OffsetDateTime
        cfg.setJsonSupport(new JacksonJsonSupport(new JavaTimeModule()));

        InputStream ksStream = new ClassPathResource(keyStorePath).getInputStream();
        cfg.setKeyStore(ksStream);
        cfg.setKeyStorePassword(keyStorePassword);

        return new SocketIOServer(cfg);
    }

    @Bean
    public ApplicationRunner runner(SocketIOServer server) {
        return args -> {
            server.start();
            log.info("Socket.IO (WSS) запущен на 0.0.0.0:{}", port);
        };
    }
}