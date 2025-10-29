package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Repository.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public Subscriber findOrCreateSubscriber(Long chatId, org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        Subscriber subscriber = subscriberRepository.findByTelegramUserId(telegramUser.getId());
        if(subscriber != null) {
            subscriber.setChatId(chatId);
            return subscriberRepository.save(subscriber);
        } else {
            return createNewSubscriber(chatId, telegramUser);
        }
    }

    public void activateUserParticipation(Long chatId) {
        // Активация участия
        Subscriber subscriber = subscriberRepository.findByChatId(chatId);

        if(!subscriber.isActive()) {
            subscriber.setActive(true);
            subscriberRepository.save(subscriber);
        } else {
            subscriber.setActive(false);
            subscriberRepository.save(subscriber);
        }
    }

    public boolean isUserActive(Long chatId) {
        // Проверка статуса пользователя
        Subscriber subscriber = subscriberRepository.findByChatId(chatId);
        return subscriber.isActive();
    }

    private Subscriber createNewSubscriber(Long chatId, org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        Subscriber subscriber = new Subscriber();
        subscriber.setTelegramUserId(telegramUser.getId());
        subscriber.setChatId(chatId);
        subscriber.setFirstName(telegramUser.getFirstName());
        subscriber.setLastName(telegramUser.getLastName());
        subscriber.setUserName(telegramUser.getUserName());
        subscriber.setActive(false);
        subscriber.setRegisteredAt(LocalDateTime.now());

        return subscriberRepository.save(subscriber);
    }
}
