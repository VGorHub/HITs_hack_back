package ru.gigastack.ai_reminder_back.notification.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.service.WebSocketGateway;

@Slf4j
@Component
@Primary                   // если позже появится реальный бин — уберёшь эту аннотацию
public class WebSocketGatewayNoOp implements WebSocketGateway {

    @Override
    public void pushToUser(Long userId, ReminderNotification payload) {
        log.info("[NO-OP WS] → user={} {}", userId, payload);
    }
}