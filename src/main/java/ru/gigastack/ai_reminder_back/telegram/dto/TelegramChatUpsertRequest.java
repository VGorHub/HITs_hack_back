package ru.gigastack.ai_reminder_back.telegram.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO, которым бот сообщает связку tg_id ↔ chat_id.
 */
public record TelegramChatUpsertRequest(

        @NotBlank
        @Pattern(regexp = "\\d+")                    // только цифры
        @Schema(example = "123456789",
                description = "tg_id пользователя (строка цифр)")
        String tgId,

        @NotNull
        @Schema(example = "987654321",
                description = "ID чата, куда реально слать сообщения")
        Long chatId
) {}