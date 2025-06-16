package ru.gigastack.ai_reminder_back.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;

/**
 * Настройка OpenAPI / Swagger-UI.
 * Определяем схему безопасности «BearerAuth» (JWT в заголовке Authorization).
 */
@OpenAPIDefinition(
        info = @Info(
                title       = "AI-Reminder API",
                version     = "v1",
                description = "Backend-сервис напоминаний"
        )
)
@SecurityScheme(
        name         = "BearerAuth",       // << это имя используем в @SecurityRequirement
        type         = SecuritySchemeType.HTTP,
        scheme       = "bearer",
        bearerFormat = "JWT",
        in           = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    /* пустой класс-конфигурация, нужны только аннотации */
}
