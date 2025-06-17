package ru.gigastack.ai_reminder_back.reminder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.gigastack.ai_reminder_back.exception.ApiError;
import ru.gigastack.ai_reminder_back.reminder.dto.*;
import ru.gigastack.ai_reminder_back.reminder.service.ReminderService;
import ru.gigastack.ai_reminder_back.service.UserService;

import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
@Tag(name = "Напоминания", description = "CRUD + список предстоящих")
public class ReminderController {

    private final ReminderService service;
    private final UserService userService;
    /* ---------- util ---------- */

    /** Достаём userId, который фильтр положил в principal (Long или String). */
    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException("Unauthenticated");

        Object det = auth.getDetails();
        if (det instanceof Long id) return id;

        // safety-fallback: username → id
        return userService.getByUsername(auth.getName()).getId();
    }

    /* ---------- create ---------- */

    @Operation(
            summary     = "Создать напоминание",
            description = "Время (scheduledAt) ожидается **в любом TZ**: ISO-8601 → хранится в БД как TIMESTAMPTZ.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReminderRequest.class),
                            examples = @ExampleObject(name = "Пример", value = """
                                    {
                                      "title": "Позвонить бабушке",
                                      "description": "Поздравить с днём рождения",
                                      "scheduledAt": "2025-06-17T13:04:00+07:00",
                                      "location": "Телефон"
                                    }""")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Создано",
                            content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибки валидации",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ReminderResponse> create(@RequestBody @Valid ReminderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(getUserId(), request));
    }

    /* ---------- read one ---------- */

    @Operation(
            summary   = "Получить напоминание по id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдено / чужое",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GetMapping("/{id}")
    public ReminderResponse get(@PathVariable Long id) {
        return service.get(getUserId(), id);
    }

    /* ---------- list all ---------- */

    @Operation(
            summary   = "Список ВСЕХ напоминаний пользователя (прошлые + будущие)",
            responses = @ApiResponse(responseCode = "200", description = "OK")
    )
    @GetMapping
    public List<ReminderResponse> list() {
        return service.list(getUserId());
    }

    /* ---------- update ---------- */

    @Operation(
            summary   = "Обновить напоминание",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдено",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PutMapping("/{id}")
    public ReminderResponse update(@PathVariable Long id,
                                   @RequestBody @Valid ReminderRequest request) {
        return service.update(getUserId(), id, request);
    }

    /* ---------- delete ---------- */

    @Operation(
            summary   = "Удалить напоминание",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Удалено"),
                    @ApiResponse(responseCode = "404", description = "Не найдено",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(getUserId(), id);
    }

    /* ---------- upcoming ---------- */

    @Operation(
            summary     = "Список предстоящих (scheduledAt > NOW, state=ACTIVE)",
            description = "Используется ботом: «что у меня сегодня?»",
            responses   = @ApiResponse(responseCode = "200", description = "OK")
    )
    @GetMapping("/upcoming")
    public List<ReminderResponse> upcoming() {
        return service.listUpcoming(getUserId());
    }
}