package ru.gigastack.ai_reminder_back.telegram;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gigastack.ai_reminder_back.common.ConflictException;
import ru.gigastack.ai_reminder_back.exception.ApiError;
import ru.gigastack.ai_reminder_back.models.User;
import ru.gigastack.ai_reminder_back.service.UserService;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ProfileController {

    private final UserService userService;

    @PatchMapping("/tg-id")
    @Operation(
            summary = "Привязать Telegram-аккаунт к профилю",
            description = "tg-id должен быть уникальным в системе. " +
                    "Возвращает привязанный tg-id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema =
                            @Schema(implementation = TgIdResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Уже занят",
                            content = @Content(schema =
                            @Schema(implementation = ApiError.class)))
            })
    public ResponseEntity<TgIdResponse> setTgId(@RequestParam String tgId) {

        User me = userService.getCurrentUser();

        // уже мой — просто подтверждаем
        if (Objects.equals(me.getTgId(), tgId)) {
            return ResponseEntity.ok(new TgIdResponse(tgId));
        }

        // чей-то чужой
        if (userService.existsByTgId(tgId)) {
            throw new ConflictException("Этот tg_id уже привязан к другому пользователю");
        }

        me.setTgId(tgId);
        userService.save(me);
        return ResponseEntity.ok(new TgIdResponse(tgId));
    }

    /* ↓ маленький DTO-ответ */
    public record TgIdResponse(String tgId) {}
}