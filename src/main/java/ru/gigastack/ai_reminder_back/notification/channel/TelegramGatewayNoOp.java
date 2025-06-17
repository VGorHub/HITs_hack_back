package ru.gigastack.ai_reminder_back.notification.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.service.TelegramGateway;

@Slf4j
@Component
@Primary
public class TelegramGatewayNoOp implements TelegramGateway {

    @Override
    public void push(Long userId, ReminderNotification payload) {
        log.info("[NO-OP TG] → user={} {}", userId, payload);
    }
}