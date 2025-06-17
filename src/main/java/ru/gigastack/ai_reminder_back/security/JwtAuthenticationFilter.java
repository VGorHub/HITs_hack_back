package ru.gigastack.ai_reminder_back.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token) && tokenProvider.validate(token)) {

                String username = tokenProvider.getUsername(token); // subject
                Long   userId   = tokenProvider.getUserId(token);   // claim "id"

                var auth = new UsernamePasswordAuthenticationToken(
                        username,                 // principal  ← ВСЕГДА username
                        null,                     // credentials
                        tokenProvider.getAuthorities(token)
                );
                auth.setDetails(userId);          // details     ← ВСЕГДА id
                auth.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));   // если нужен WebDetails — можно в Map

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            log.warn("JWT auth failed: {}", ex.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 1) Header  Authorization: Bearer &lt;jwt&gt;<br>
     * 2) Query-param ?token=&lt;jwt&gt;  — используется браузером при WebSocket-handshake.
     */
    private String resolveToken(HttpServletRequest req) {
        // #1  — стандартный вариант
        String bearer = req.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        // #2  — fallback для /ws?token=<jwt>
        String qp = req.getParameter("token");
        return StringUtils.hasText(qp) ? qp : null;
    }
}