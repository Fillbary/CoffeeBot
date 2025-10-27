package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.User;
import com.example.CoffeeBot.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreateUser(Long chatId, org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        User user = userRepository.findByTelegramUserId(telegramUser.getId());
        if(user != null) {
            user.setChatId(chatId);
            return userRepository.save(user);
        } else {
            return createNewUser(chatId, telegramUser);
        }
    }

    public void activateUserParticipation(Long chatId) {
        // Активация участия + валидация
        User user = userRepository.findByChatId(chatId);
        user.setActive(true);
        userRepository.save(user);
    }

    public void deactivateUser(Long chatId) {
        // Деактивация пользователя
        User user = userRepository.findByChatId(chatId);
        user.setActive(false);
        userRepository.save(user);
    }

    public boolean isUserActive(Long chatId) {
        // Проверка статуса пользователя
        User user = userRepository.findByChatId(chatId);
        return user.isActive();
    }

    private User createNewUser(Long chatId, org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        User user = new User();
        user.setTelegramUserId(telegramUser.getId());
        user.setChatId(chatId);
        user.setFirstName(telegramUser.getFirstName());
        user.setLastName(telegramUser.getLastName());
        user.setUserName(telegramUser.getUserName());
        user.setActive(false);
        user.setRegisteredAt(LocalDateTime.now());
        return user;
    }
}
