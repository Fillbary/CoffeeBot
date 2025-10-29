package com.example.CoffeeBot.Repository;

import com.example.CoffeeBot.Entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Subscriber findByTelegramUserId(Long id);

    Subscriber findByChatId(Long id);
}
