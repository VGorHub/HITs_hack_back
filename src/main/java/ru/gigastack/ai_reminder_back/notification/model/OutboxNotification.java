package ru.gigastack.ai_reminder_back.notification.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "outbox_notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OutboxNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reminderId;
    private Long userId;

    /** JSON-payload теперь хранится в TEXT, чтобы не обрезать длинные строки */
    @Column(columnDefinition = "TEXT")
    private String payload;

    private boolean processed;
    private ZonedDateTime createdAt;
}