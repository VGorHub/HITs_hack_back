package ru.gigastack.ai_reminder_back.notification.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * То, что реально уходит во фронт / Telegram.
 * Здесь только те данные, которые нужны каналу доставки.
 */
@Value
@Builder
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ReminderNotification {
    private Long   reminderId;
    private Long   userId;
    private String title;
    private String description;
    private OffsetDateTime scheduledAt;
}