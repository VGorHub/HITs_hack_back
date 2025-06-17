package ru.gigastack.ai_reminder_back.notification.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "outbox_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reminderId;

    private Long userId;

    private String payload; // JSON payload for delivery

    private boolean processed;

    private ZonedDateTime createdAt;
}
