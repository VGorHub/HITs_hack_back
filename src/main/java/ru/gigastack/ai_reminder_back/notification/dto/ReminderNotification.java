package ru.gigastack.ai_reminder_back.notification.dto;

import java.time.OffsetDateTime;

/**
 * То, что реально уходит во фронт / Telegram.
 * Здесь только те данные, которые нужны каналу доставки.
 */
public record ReminderNotification(
        Long          reminderId,
        Long          userId,
        String        title,
        String        description,
        OffsetDateTime scheduledAt,
        String        location
) {}