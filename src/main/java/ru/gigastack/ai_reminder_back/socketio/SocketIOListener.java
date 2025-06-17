package ru.gigastack.ai_reminder_back.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gigastack.ai_reminder_back.security.JwtTokenProvider;

/**
 * Валидация JWT (?token=…) и помещение клиента
 * в комнату «user-{id}».
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOListener {

    private final SocketIOServer   server;
    private final JwtTokenProvider tokenProvider;

    @PostConstruct
    public void init() {

        server.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if (token == null || !tokenProvider.validate(token)) {          // ← validate()
                client.disconnect();
                log.warn("[ws] rejected – bad token");
                return;
            }
            Long userId = tokenProvider.getUserId(token);
            client.joinRoom(room(userId));
            log.debug("[ws] {} joined {}", client.getSessionId(), room(userId));
        });

        server.addDisconnectListener(c ->
                log.debug("[ws] {} disconnected", c.getSessionId()));
    }

    /** room-helper: «user-{id}» */
    public static String room(Long userId) {
        return "user-" + userId;
    }
}