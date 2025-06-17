package ru.gigastack.ai_reminder_back.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.model.OutboxNotification;
import ru.gigastack.ai_reminder_back.notification.repository.OutboxRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxNotifier {

    private final OutboxRepository repo;
    private final WebSocketGateway webSocketGateway;
    private final TelegramGateway  telegramGateway;
    private final ObjectMapper     mapper = new ObjectMapper();

    /** Каждые 5 сек проверяем «созрели» ли записи. */
    @Scheduled(fixedDelay = 5_000)
    @Transactional
    public void dispatch() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        repo.findReady(now).forEach(this::sendAndMark);
    }

    private void sendAndMark(OutboxNotification on) {
        ReminderNotification payload = toReminderNotification(on);

        boolean ok = true;
        try {
            webSocketGateway.pushToUser(on.getUserId(), payload);
            telegramGateway.push(on.getUserId(), payload);
        } catch (Exception e) {
            ok = false;
            log.error("Ошибка при отправке reminderId={}", on.getReminderId(), e);
        }

        if (ok) {
            on.setProcessed(true);
            repo.save(on);
        }
    }

    private ReminderNotification toReminderNotification(OutboxNotification on) {
        try {
            return mapper.readValue(on.getPayload(), ReminderNotification.class);
        } catch (Exception e) {
            log.warn("Bad payload, fallback dto. id={}", on.getId(), e);
            return ReminderNotification.builder()
                    .reminderId(on.getReminderId())
                    .userId(on.getUserId())
                    .title("(unknown)")
                    .build();
        }
    }
}