package ru.gigastack.ai_reminder_back.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.gigastack.ai_reminder_back.dto.JwtAuthenticationResponse;
import ru.gigastack.ai_reminder_back.dto.SignInRequest;
import ru.gigastack.ai_reminder_back.dto.SignUpRequest;
import ru.gigastack.ai_reminder_back.security.AuthenticationService;
import ru.gigastack.ai_reminder_back.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService           service;


    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }


    @Operation(
            summary = "Пример – нужен авторизованный пользователь",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @GetMapping
    public String example() {
        return "Hello, world!";
    }

    @Operation(
            summary = "Пример – нужна роль ADMIN",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String exampleAdmin() {
        return "Hello, admin!";
    }

    @Operation(
            summary = "Получить роль ADMIN (для демонстрации)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @GetMapping("/get-admin")
    public void getAdmin() {
        service.getAdmin();
    }
}
