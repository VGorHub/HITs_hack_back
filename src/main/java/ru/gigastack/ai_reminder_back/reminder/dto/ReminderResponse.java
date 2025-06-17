package ru.gigastack.ai_reminder_back.reminder.dto;

import ru.gigastack.ai_reminder_back.reminder.model.*;
import java.time.*;

public record ReminderResponse(
        Long id,
        String title,
        String description,
        OffsetDateTime scheduledAt,
        String location,
        ReminderState state,
        ReminderSource source
) {}
