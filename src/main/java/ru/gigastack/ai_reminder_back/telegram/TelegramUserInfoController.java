package ru.gigastack.ai_reminder_back.telegram;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gigastack.ai_reminder_back.common.NotFoundException;
import ru.gigastack.ai_reminder_back.exception.ApiError;
import ru.gigastack.ai_reminder_back.repository.UserRepository;
import ru.gigastack.ai_reminder_back.telegram.model.TelegramChat;
import ru.gigastack.ai_reminder_back.telegram.repository.TelegramChatRepository;

@RestController
@RequestMapping("/internal/telegram")
@RequiredArgsConstructor
public class TelegramUserInfoController {

    private final UserRepository         userRepo;
    private final TelegramChatRepository chatRepo;

    @GetMapping("/user-info")
    @Operation(
            summary = "Проверка существования пользователя по tg_id",
            description = """
                    Возвращает chatId (если бот уже видел юзера) и username.
                    • 200 – пользователь найден  
                    • 404 – пользователя с таким tg_id нет
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema =
                            @Schema(implementation = UserInfoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найден",
                            content = @Content(schema =
                            @Schema(implementation = ApiError.class)))
            }
    )
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestParam String tgId) {

        var user = userRepo.findByTgId(tgId)
                .orElseThrow(() ->
                        new NotFoundException("User with tg_id %s not found".formatted(tgId)));

        Long chatId = chatRepo.findByTgId(tgId)
                .map(TelegramChat::getChatId)
                .orElse(null);

        return ResponseEntity.ok(new UserInfoResponse(user.getUsername(), chatId));
    }

    /* компактный DTO-ответ */
    public record UserInfoResponse(String username, Long chatId) {}
}