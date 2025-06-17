package ru.gigastack.ai_reminder_back.reminder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.gigastack.ai_reminder_back.models.User;
import ru.gigastack.ai_reminder_back.reminder.dto.*;
import ru.gigastack.ai_reminder_back.reminder.service.ReminderService;

import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService service;

    // For hackathon simplicity we take userId from header; replace with auth later
    /*private Long getUserIdFromHeader(HttpHeaders headers) {
        return Long.valueOf(headers.getFirst("X-User-Id"));
    }*/
    private Long getUserId() {
        return ((User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getId();
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> create(@RequestHeader HttpHeaders headers,
                                                   @RequestBody @Valid ReminderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(getUserId(), request));
    }

    @GetMapping("/{id}")
    public ReminderResponse get(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        return service.get(getUserId(), id);
    }

    @GetMapping
    public List<ReminderResponse> list(@RequestHeader HttpHeaders headers) {
        return service.list(getUserId());
    }

    @PutMapping("/{id}")
    public ReminderResponse update(@RequestHeader HttpHeaders headers,
                                   @PathVariable Long id,
                                   @RequestBody @Valid ReminderRequest request) {
        return service.update(getUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        service.delete(getUserId(), id);
    }

    @Operation(summary = "Список будущих напоминаний",
            security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/upcoming")
    public List<ReminderResponse> upcoming() {
        return service.listUpcoming(getUserId());
    }
}
