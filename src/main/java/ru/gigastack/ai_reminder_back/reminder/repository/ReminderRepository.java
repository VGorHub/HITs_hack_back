package ru.gigastack.ai_reminder_back.reminder.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.gigastack.ai_reminder_back.reminder.model.*;

import java.time.*;
import java.util.*;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUserId(Long userId);

    List<Reminder> findByStateAndScheduledAtBefore(ReminderState state,
                                                   OffsetDateTime before);
}
