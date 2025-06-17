package ru.gigastack.ai_reminder_back.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Глобальная конфигурация OpenAPI 3 (swagger-ui).
 *
 * ⚠️ Никакой логики — только аннотации.
 */
@OpenAPIDefinition(
        info = @Info(
                title       = "AI-Reminder API",
                version     = "v1",
                description = """
                        **Микро-backend личного ассистента / напоминалок**

                        * авторизация — JWT (Bearer)  
                        * CRUD по напоминаниям  
                        * Outbox-механизм -> WebSocket / Telegram  
                        * роли: USER / ADMIN
                        """,
                contact = @Contact(
                        name  = "Backend team",
                        email = "dev@gigastack.ru",
                        url   = "https://github.com/VGorHub/HITs_hack_back"
                ),
                license = @License(
                        name = "MIT",
                        url  = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://192.168.100.58:8080", description = "Local DEV"),
                @Server(url = "https://api.ai-reminder.dev", description = "Prod")
        }
)
@SecurityScheme(
        name         = "BearerAuth",
        type         = SecuritySchemeType.HTTP,
        scheme       = "bearer",
        bearerFormat = "JWT",
        in           = SecuritySchemeIn.HEADER
)
public class OpenApiConfig { /* пусто */ }