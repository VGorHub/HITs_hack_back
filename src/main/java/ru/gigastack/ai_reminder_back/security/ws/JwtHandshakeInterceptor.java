package ru.gigastack.ai_reminder_back.security.ws;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ru.gigastack.ai_reminder_back.security.JwtTokenProvider;

import java.util.Map;

/**
 * Поднимает аутентификацию на этапе WS-handshake.<br>
 * Источник токена: <br>
 *   1) Header <code>Authorization: Bearer &lt;jwt&gt;</code>  <br>
 *   2) query-param <code>?token=&lt;jwt&gt;</code> — удобно для SockJS.
 */
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(@NonNull  ServerHttpRequest  request,
                                   @NonNull  ServerHttpResponse response,
                                   @NonNull  WebSocketHandler   wsHandler,
                                   @NonNull  Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest http = servletRequest.getServletRequest();
            String token = resolveToken(http);

            if (StringUtils.hasText(token) && tokenProvider.validate(token)) {
                var auth = new UsernamePasswordAuthenticationToken(
                        tokenProvider.getUserId(token).toString(),   // principal — строкой
                        null,
                        tokenProvider.getAuthorities(token)
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(http));
                SecurityContextHolder.getContext().setAuthentication(auth);

                /* важно: положим Authentication в атрибуты сессии,
                   чтобы Spring восстановил Principal для последующих STOMP-кадров */
                attributes.put("SPRING.AUTHENTICATION", auth);
            }
        }
        return true;   // всегда пропускаем handshake, даже если токена нет
    }

    @Override public void afterHandshake(@NonNull  ServerHttpRequest  request,
                                         @NonNull  ServerHttpResponse response,
                                         @NonNull  WebSocketHandler   wsHandler,
                                         @Nullable Exception ex) { /* no-op */ }

    /* ---------- util ---------- */

    private String resolveToken(HttpServletRequest req) {
        String bearer = req.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return req.getParameter("token");   // fallback
    }
}