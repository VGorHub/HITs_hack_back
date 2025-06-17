package ru.gigastack.ai_reminder_back.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Адаптер над существующим JwtService, чтобы фильтр ничего не знал
 * о деталях реализации.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtService jwtService;   // ← уже есть в проекте

    public boolean validate(String token) {
        return jwtService.validateToken(token);
    }

    public Long getUserId(String token) {
        return jwtService.getUserId(token);
    }
    public String getUsername(String token) {          // новый метод
        return jwtService.extractUserName(token);
    }
    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        // из обёртки JwtService получаем роль строкой
        String role = jwtService.getAuthorities(token);   // ROLE_USER, ROLE_ADMIN …
        return List.of(new SimpleGrantedAuthority(role));
    }

}