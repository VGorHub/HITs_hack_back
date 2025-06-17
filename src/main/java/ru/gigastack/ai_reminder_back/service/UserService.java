package ru.gigastack.ai_reminder_back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.gigastack.ai_reminder_back.models.Role;
import ru.gigastack.ai_reminder_back.models.User;
import ru.gigastack.ai_reminder_back.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    public User save(User user) {
        return repository.save(user);
    }


    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            // Заменить на свои исключения
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

       /* if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }*/

        return save(user);
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new UsernameNotFoundException("Unauthenticated");

        // сначала пытаемся по id, который гарантированно лежит в details
        Object det = auth.getDetails();
        if (det instanceof Long id) {
            return repository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        }

        // fallback: ищем по username (principal)
        return repository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }


    /**
     * Выдача прав администратора текущему пользователю
     * <p>
     * Нужен для демонстрации
     */
    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }


    public boolean existsByTgId(String tgId) {          // ← String
        return repository.existsByTgId(tgId);
    }

    public Optional<User> findByTgId(String tgId) {     // ← String
        return repository.findByTgId(tgId);
    }
}