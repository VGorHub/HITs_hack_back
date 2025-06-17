package ru.gigastack.ai_reminder_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AiReminderBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiReminderBackApplication.class, args);
	}

}
