package ru.gigastack.ai_reminder_back.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigastack.ai_reminder_back.models.User;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.model.OutboxNotification;
import ru.gigastack.ai_reminder_back.notification.repository.OutboxRepository;
import ru.gigastack.ai_reminder_back.repository.UserRepository;
import ru.gigastack.ai_reminder_back.telegram.repository.TelegramChatRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxNotifier {

    private final OutboxRepository       repo;
    private final UserRepository         userRepo;
    private final TelegramChatRepository chatRepo;
    private final WebSocketGateway       webSocketGateway;
    private final TelegramGateway        telegramGateway;
    /** ← берём уже настроенный spring-овский mapper (JavaTimeModule + ISO-8601) */
    private final ObjectMapper           mapper;

    @Scheduled(fixedDelay = 5_000)
    @Transactional
    public void dispatch() {
        repo.findReady(OffsetDateTime.now(ZoneOffset.UTC)).forEach(this::sendAndMark);
    }

    private void sendAndMark(OutboxNotification on) {

        ReminderNotification payload = toReminderNotification(on);
        boolean ok = true;

        /* -------- WebSocket -------- */
        try {
            webSocketGateway.pushToUser(on.getUserId(), payload);
        } catch (Exception e) {
            ok = false;
            log.error("WS error, reminderId={}", on.getReminderId(), e);
        }

        /* -------- Telegram -------- */
        try {
            userRepo.findById(on.getUserId())
                    .map(User::getTgId)              // String
                    .flatMap(chatRepo::findByTgId)   // String → Optional<TelegramChat>
                    .ifPresent(chat -> telegramGateway.push(chat.getChatId(), payload));
        } catch (Exception e) {
            ok = false;
            log.error("TG error, reminderId={}", on.getReminderId(), e);
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
            return new ReminderNotification(
                    on.getReminderId(),
                    on.getUserId(),
                    "(unknown)",
                    null,
                    null,
                    null              // <-- location по-умолчанию
            );
        }
    }
}