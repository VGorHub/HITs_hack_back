package ru.gigastack.ai_reminder_back.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Глобальный перехват ошибок MVC + Spring Security.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ---------- 400: ошибки валидации ---------- */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req) {

        String msg = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(this::fieldErrorToString)
                .collect(Collectors.joining("; "));

        return build(HttpStatus.BAD_REQUEST, msg, req, ex);
    }

    /* ---------- 401: проблемы аутентификации ---------- */

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(
            AuthenticationException ex,
            HttpServletRequest req) {

        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, ex);
    }

    /* ---------- 500: всё остальное ---------- */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(
            Exception ex,
            HttpServletRequest req) {

        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal error", req, ex);
    }

    /* ---------- util ---------- */

    private ResponseEntity<ApiError> build(HttpStatus status,
                                           String message,
                                           HttpServletRequest req,
                                           Exception ex) {

        ApiError body = ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(req.getMethod() + " " + req.getRequestURI())
                .build();

        if (status.is5xxServerError()) {
            log.error("{} → {} {}", body.getPath(), status.value(), message, ex);
        } else {
            log.warn("{} → {} {}", body.getPath(), status.value(), message);
        }

        return ResponseEntity.status(status).body(body);
    }

    private String fieldErrorToString(FieldError e) {
        return "%s: %s".formatted(e.getField(), e.getDefaultMessage());
    }
}
