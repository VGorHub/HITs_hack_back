package ru.gigastack.ai_reminder_back.reminder.mapper;

import org.mapstruct.*;
import ru.gigastack.ai_reminder_back.reminder.model.*;
import ru.gigastack.ai_reminder_back.reminder.dto.*;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "userId",      ignore = true)
    @Mapping(target = "state",       ignore = true)
    @Mapping(target = "source",      ignore = true)
    @Mapping(target = "externalCalendarEventId", ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    Reminder toEntity(ReminderRequest dto);

    ReminderResponse toDto(Reminder entity);
}