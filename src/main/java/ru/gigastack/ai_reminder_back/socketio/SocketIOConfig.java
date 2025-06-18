package ru.gigastack.ai_reminder_back.socketio;

import com.corundumstudio.socketio.SocketIOServer;
//  НЕ импортируем com.corundumstudio.socketio.Configuration, чтобы имена не конфликтовали
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;   // ← обычный импорт Spring

@Configuration                       // используем аннотацию напрямую
@RequiredArgsConstructor
@Slf4j
public class SocketIOConfig {

    @Value("${socketio.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration cfg =
                new com.corundumstudio.socketio.Configuration();  // полное имя класса
        cfg.setHostname("0.0.0.0");
        cfg.setPort(port);
        return new SocketIOServer(cfg);
    }

    @Bean
    public ApplicationRunner runner(SocketIOServer server) {
        return args -> {
            server.start();
            log.info("✅ Socket.IO сервер запущен на порту {}", port);
        };
    }
}