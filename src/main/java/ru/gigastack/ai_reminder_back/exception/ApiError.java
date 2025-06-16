package ru.gigastack.ai_reminder_back.exception;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Единый формат отправки ошибок клиенту.
 */
@Data
@Builder
public class ApiError {

    /** Момент формирования ответа */
    private OffsetDateTime timestamp;

    /** HTTP-код */
    private int            status;

    /** Текстовое название статуса (Bad Request, Forbidden …) */
    private String         error;

    /** Детали ошибки для клиента */
    private String         message;

    /** Запрошенный путь */
    private String         path;
}
