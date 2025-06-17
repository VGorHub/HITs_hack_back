package ru.gigastack.ai_reminder_back.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Встроенный (embedded) сервер Socket.IO на Netty. */
@Configuration
@Slf4j
public class SocketIOConfig {

    @Value("${socketio.port:9092}")
    private Integer port;

    @Bean(destroyMethod = "stop")
    public SocketIOServer socketIOServer() {
        var cfg = new com.corundumstudio.socketio.Configuration();
        cfg.setPort(port);
        cfg.setOrigin("*");                // CORS: любой origin
        return new SocketIOServer(cfg);
    }

    /** Авто-старт после инициализации Spring-контекста. */
    @Bean
    public ApplicationRunner runner(SocketIOServer server) {
        return args -> {
            server.start();
            log.info("Socket.IO started on 0.0.0.0:{}", port);
        };
    }
}