package ru.gigastack.ai_reminder_back.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.gigastack.ai_reminder_back.dto.JwtAuthenticationResponse;
import ru.gigastack.ai_reminder_back.dto.SignInRequest;
import ru.gigastack.ai_reminder_back.dto.SignUpRequest;
import ru.gigastack.ai_reminder_back.exception.ApiError;
import ru.gigastack.ai_reminder_back.security.AuthenticationService;
import ru.gigastack.ai_reminder_back.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Регистрация, логин, примеры защищённых эндпоинтов")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService           service;

    /* ---------- sign-up ---------- */

    @Operation(
            summary     = "Регистрация пользователя",
            description = "Создаёт запись в таблице *users* и сразу возвращает JWT.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignUpRequest.class),
                            examples = @ExampleObject(name = "Пример", value = """
                                    {
                                      "username": "jon_doe",
                                      "password": "my_1secret1_password"
                                    }""")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "OK (регистрация прошла)",
                            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Валидация / пользователь уже существует",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    /* ---------- sign-in ---------- */

    @Operation(
            summary     = "Авторизация пользователя",
            description = "Принимает логин/пароль, возвращает свежий JWT.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignInRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Неверный логин/пароль",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    /* ---------- demo endpoints ---------- */

    @Operation(
            summary = "Echo (нужна любая авторизация)",
            security = @SecurityRequirement(name = "BearerAuth"),
            responses = @ApiResponse(responseCode = "200", description = "Просто строка")
    )
    @GetMapping
    public String example() {
        return "Hello, world!";
    }

    @Operation(
            summary = "Только для ADMIN",
            security = @SecurityRequirement(name = "BearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Вы — админ"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String exampleAdmin() {
        return "Hello, admin!";
    }

    @Operation(
            summary   = "Выдать текущему пользователю роль ADMIN (демо)",
            security  = @SecurityRequirement(name = "BearerAuth"),
            responses = @ApiResponse(responseCode = "200", description = "OK")
    )
    @GetMapping("/get-admin")
    public void getAdmin() {
        service.getAdmin();
    }
}