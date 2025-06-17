package ru.gigastack.ai_reminder_back.telegram.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "telegram_chats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TelegramChat {

    /** tg-user id — PRIMARY KEY */
    @Id
    @Column(name = "tg_id")
    private String tgId;

    /** chat_id, куда реально слать сообщения через Bot API */
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
}