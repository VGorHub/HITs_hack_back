package ru.gigastack.ai_reminder_back.reminder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigastack.ai_reminder_back.common.NotFoundException;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.model.OutboxNotification;
import ru.gigastack.ai_reminder_back.notification.repository.OutboxRepository;
import ru.gigastack.ai_reminder_back.reminder.dto.*;
import ru.gigastack.ai_reminder_back.reminder.mapper.ReminderMapper;
import ru.gigastack.ai_reminder_back.reminder.model.*;
import ru.gigastack.ai_reminder_back.reminder.repository.ReminderRepository;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository repository;
    private final OutboxRepository   outboxRepository;
    private final ReminderMapper     mapper;
    /** Берём уже настроенный spring-овский mapper (JavaTimeModule + ISO-8601). */
    private final ObjectMapper       mapperJson;

    /* ---------- create ---------- */
    @Override
    @Transactional
    public ReminderResponse create(Long userId, ReminderRequest req) {

        Reminder reminder = mapper.toEntity(req);
        reminder.setUserId(userId);
        reminder.setState(ReminderState.ACTIVE);
        reminder.setSource(ReminderSource.WEB);
        reminder.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Reminder saved = repository.save(reminder);

        /* ––– payload для каналов доставки ––– */
        ReminderNotification dto = new ReminderNotification(
                saved.getId(),
                userId,
                saved.getTitle(),
                saved.getDescription(),
                saved.getScheduledAt(),
                saved.getLocation()
        );

        String json;
        try {
            json = mapperJson.writeValueAsString(dto);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize ReminderNotification", e);
        }

        /* ––– outbox ––– */
        OutboxNotification on = OutboxNotification.builder()
                .reminderId(saved.getId())
                .userId(userId)
                .payload(json)
                .processed(false)
                .createdAt(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
        outboxRepository.save(on);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReminderResponse get(Long userId, Long id) {
        Reminder r = repository.findById(id)
                .filter(rem -> rem.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Reminder not found"));
        return mapper.toDto(r);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> list(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ReminderResponse update(Long userId, Long id, ReminderRequest req) {
        Reminder reminder = repository.findById(id)
                .filter(rem -> rem.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Reminder not found"));

        reminder.setTitle(req.title());
        reminder.setDescription(req.description());
        reminder.setScheduledAt(req.scheduledAt());
        reminder.setLocation(req.location());
        Reminder saved = repository.save(reminder);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        Reminder reminder = repository.findById(id)
                .filter(rem -> rem.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Reminder not found"));
        repository.delete(reminder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> listUpcoming(Long userId) {
        return repository.findUpcoming(userId, OffsetDateTime.now(ZoneOffset.UTC))
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
