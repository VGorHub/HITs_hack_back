package ru.gigastack.ai_reminder_back.reminder.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "scheduled_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime scheduledAt;

    private String location;  // optional textual location

    @Enumerated(EnumType.STRING)
    private ReminderState state;

    @Enumerated(EnumType.STRING)
    private ReminderSource source;

    private String externalCalendarEventId; // e.g., Google Calendar event ID

    @Column(name = "created_at",   columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;
}
