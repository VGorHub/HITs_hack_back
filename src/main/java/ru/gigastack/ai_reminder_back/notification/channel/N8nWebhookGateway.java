package ru.gigastack.ai_reminder_back.notification.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.gigastack.ai_reminder_back.notification.dto.ReminderNotification;
import ru.gigastack.ai_reminder_back.notification.service.TelegramGateway;

import java.util.Map;

@Component @Primary
@RequiredArgsConstructor
@Slf4j

public class N8nWebhookGateway implements TelegramGateway {

    @Value("${n8n.webhook.url}")
    private String webhookUrl;

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public void push(Long chatId, ReminderNotification payload) {
        try {
            webClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("chat_id", chatId, "notification", payload))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.debug("[n8n] → {} {}", chatId, payload);
        } catch (Exception e) {
            // WARN вместо ERROR, чтоб не краснело
            log.warn("[n8n] push failed ({}): {}", webhookUrl, e.getMessage());
        }
    }
}