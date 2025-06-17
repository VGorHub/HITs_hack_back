package ru.gigastack.ai_reminder_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gigastack.ai_reminder_back.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    /* --- tg_id теперь хранится в виде строки --- */
    Optional<User> findByTgId(String tgId);
    boolean existsByTgId(String tgId);
}