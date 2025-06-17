package ru.gigastack.ai_reminder_back.notification.service;

import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;

public interface WebSocketGateway {
    /** Отправить конкретному пользователю. */
    void pushToUser(Long userId, ReminderNotification payload);
}