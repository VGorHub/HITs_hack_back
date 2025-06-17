package ru.gigastack.ai_reminder_back.notification.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.gigastack.ai_reminder_back.config.WebSocketProps;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.service.WebSocketGateway;

@Slf4j
@Component
@Primary      // переопределяет No-Op-бин
@RequiredArgsConstructor
public class WebSocketGatewayStomp implements WebSocketGateway {

    private final SimpMessagingTemplate template;
    private final WebSocketProps        props;

    @Override
    public void pushToUser(Long userId, ReminderNotification payload) {
        String destination = "%s/notifications/%d"
                .formatted(props.getPrefix().getTopic(), userId);   // /topic/notifications/42
        template.convertAndSend(destination, payload);
        log.debug("[WS] → {} {}", destination, payload);
    }
}