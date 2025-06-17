package ru.gigastack.ai_reminder_back.notification.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.service.WebSocketGateway;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketGatewayStomp implements WebSocketGateway {

    private final SimpMessagingTemplate template;

    /** Куда подписывается фронт: <code>/user/queue/notifications</code> */
    private static final String DESTINATION = "/queue/notifications";

    @Override
    public void pushToUser(Long userId, ReminderNotification payload) {
        template.convertAndSendToUser(userId.toString(), DESTINATION, payload);
        log.debug("[WS] → user={} {}", userId, payload);
    }
}