package ru.gigastack.ai_reminder_back.notification.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gigastack.ai_reminder_back.notification.model.OutboxNotification;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxNotification, Long> {

    /** Только те, что пора отправить – reminder уже «настал» */
    @Query("""
       select on
         from OutboxNotification on
         join Reminder r on r.id = on.reminderId
        where on.processed = false
          and r.scheduledAt <= :now
       """)
    List<OutboxNotification> findReady(@Param("now") OffsetDateTime now);
}
