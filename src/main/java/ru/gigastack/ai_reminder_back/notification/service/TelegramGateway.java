package ru.gigastack.ai_reminder_back.notification.service;

import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;

public interface TelegramGateway {
    /** chatId — настоящий chat_id Telegram-чата */
    void push(Long chatId, ReminderNotification payload);
}