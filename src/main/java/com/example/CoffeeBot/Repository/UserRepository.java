package com.example.CoffeeBot.Repository;

import com.example.CoffeeBot.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByTelegramUserId(Long id);

    User findByChatId(Long id);
}
