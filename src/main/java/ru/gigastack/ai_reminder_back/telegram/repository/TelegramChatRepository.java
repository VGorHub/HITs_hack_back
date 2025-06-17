package ru.gigastack.ai_reminder_back.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gigastack.ai_reminder_back.telegram.model.TelegramChat;

import java.util.Optional;

/**
 * CRUD по таблице telegram_chats.
 * tgId хранится строкой, поэтому ключ и методы работают c String.
 */
public interface TelegramChatRepository extends JpaRepository<TelegramChat, String> {

    Optional<TelegramChat> findByTgId(String tgId);
}