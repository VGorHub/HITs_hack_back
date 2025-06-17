package ru.gigastack.ai_reminder_back.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;

/** Отправка событий в комнату «user-{id}». */
@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOGateway {

    private final SocketIOServer server;

    public void push(Long userId, ReminderNotification payload) {
        String room = SocketIOListener.room(userId);
        server.getRoomOperations(room).sendEvent("reminder", payload);
        log.debug("[ws] → {} {}", room, payload);
    }
}