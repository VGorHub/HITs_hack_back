package ru.gigastack.ai_reminder_back.reminder.service;

import ru.gigastack.ai_reminder_back.reminder.dto.*;

import java.util.List;

public interface ReminderService {
    ReminderResponse create(Long userId, ReminderRequest req);
    ReminderResponse get(Long userId, Long id);
    List<ReminderResponse> list(Long userId);
    ReminderResponse update(Long userId, Long id, ReminderRequest req);
    void delete(Long userId, Long id);
}
