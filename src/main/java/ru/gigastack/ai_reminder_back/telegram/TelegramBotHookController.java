package ru.gigastack.ai_reminder_back.telegram;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gigastack.ai_reminder_back.exception.ApiError;
import ru.gigastack.ai_reminder_back.telegram.dto.TelegramChatUpsertRequest;
import ru.gigastack.ai_reminder_back.telegram.model.TelegramChat;
import ru.gigastack.ai_reminder_back.telegram.repository.TelegramChatRepository;

@RestController
@RequestMapping("/internal/telegram")
@RequiredArgsConstructor
public class TelegramBotHookController {

    private final TelegramChatRepository chatRepo;

    /**
     * Upsert tg_id ↔ chat_id — вызывается Telegram-ботом.
     */
    @PostMapping("/chat")
    @Operation(
            summary = "Upsert связи tg_id ↔ chat_id (вызывается ботом)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Создана новая пара"),
                    @ApiResponse(responseCode = "200", description = "Пара обновлена"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            })
    public ResponseEntity<Void> upsertChat(@RequestBody @Valid TelegramChatUpsertRequest req) {

        boolean created = chatRepo.findById(req.tgId())
                .map(chat -> {                       // UPDATE
                    chat.setChatId(req.chatId());
                    return chatRepo.save(chat);
                })
                .isEmpty();                          // INSERT — объекта ещё нет

        if (created) {
            chatRepo.save(TelegramChat.builder()
                    .tgId(req.tgId())
                    .chatId(req.chatId())
                    .build());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.ok().build();
    }
}