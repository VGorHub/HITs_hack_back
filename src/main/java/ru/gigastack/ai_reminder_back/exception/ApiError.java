package ru.gigastack.ai_reminder_back.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
@Schema(description = "Единый формат ответа при ошибке")
public class ApiError {

    @Schema(description = "ISO-дата/время на сервере", example = "2025-06-17T02:55:09.384+00:00")
    private OffsetDateTime timestamp;

    @Schema(description = "HTTP-код", example = "404")
    private int            status;

    @Schema(description = "Короткое описание кода", example = "Not Found")
    private String         error;

    @Schema(description = "Детали причины", example = "Reminder not found")
    private String         message;

    @Schema(description = "Запрошенный метод и путь", example = "GET /api/v1/reminders/42")
    private String         path;
}