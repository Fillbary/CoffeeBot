package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Repository.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    /**
     * Находит существующего подписчика по Telegram ID или создает нового
     * Обновляет chatId если подписчик уже существует
     *
     * @param chatId идентификатор чата пользователя
     * @param telegramUser объект пользователя Telegram
     * @return найденный или созданный подписчик
     */
    public Subscriber findOrCreateSubscriber(Long chatId, org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        Subscriber subscriber = subscriberRepository.findByTelegramUserId(telegramUser.getId());
        if(subscriber != null) {
            subscriber.setChatId(chatId);
            return subscriberRepository.save(subscriber);
        } else {
            return createNewSubscriber(chatId, telegramUser);
        }
    }

    /**
     * Активирует или деактивирует участие пользователя в кофе-брейках
     * Переключает текущий статус активности на противоположный
     *
     * @param chatId идентификатор чата пользователя
     */
    public void activateUserParticipation(Long chatId) {
        Subscriber subscriber = subscriberRepository.findByChatId(chatId);
        subscriber.setActive(!subscriber.isActive());
        subscriberRepository.save(subscriber);
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
