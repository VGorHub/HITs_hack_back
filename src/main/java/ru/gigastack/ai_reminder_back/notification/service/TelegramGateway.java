package ru.gigastack.ai_reminder_back.notification.service;

import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;

public interface TelegramGateway {
    void push(Long userId, ReminderNotification payload);
}