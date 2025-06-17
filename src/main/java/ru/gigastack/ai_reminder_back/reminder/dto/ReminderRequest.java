package ru.gigastack.ai_reminder_back.reminder.dto;

import jakarta.validation.constraints.*;
import java.time.*;

public record ReminderRequest(
        @NotBlank String title,
        String description,
        @NotNull OffsetDateTime scheduledAt,
        String location
) {}
