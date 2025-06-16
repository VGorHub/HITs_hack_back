package ru.gigastack.ai_reminder_back.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.gigastack.ai_reminder_back.exception.ApiError;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Делает Spring Security-ошибки (401 / 403) однотипными с MVC-ошибками.
 */
@Slf4j
@Component
public class JsonAuthHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    /* -------- 401: не аутентифицирован -------- */

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        send(HttpStatus.UNAUTHORIZED, "Unauthorized", request, response);
    }

    /* -------- 403: нет прав -------- */

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        send(HttpStatus.FORBIDDEN, "Forbidden", request, response);
    }

    /* -------- util -------- */

    private void send(HttpStatus status,
                      String message,
                      HttpServletRequest req,
                      HttpServletResponse resp) throws IOException {

        ApiError body = ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(req.getRequestURI())
                .build();

        resp.setStatus(status.value());
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), body);

        log.warn("{} {} → {} {}", req.getMethod(), req.getRequestURI(),
                status.value(), message);
    }
}
