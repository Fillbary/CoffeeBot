package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Service.CreateMessageService;
import com.example.CoffeeBot.Service.SubscriberService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class MessageHandler {
    private final SubscriberService subscriberService;
    private final CreateMessageService messageService;

    public MessageHandler(SubscriberService subscriberService, CreateMessageService messageService) {
        this.subscriberService = subscriberService;
        this.messageService = messageService;
    }

    public SendMessage handleMessage(Message message) {
        String text = message.getText();

        if ("/start".equals(text)) {
            return handleStartCommand(message.getChatId(), message.getFrom());
        } else if ("/status".equals(text)) {
            return handleStatusCommand(message.getChatId());
        }
        return new SendMessage(message.getChatId().toString(), "Unknown command");
    }

    private SendMessage handleStartCommand(Long chatId, User telegramUser) {
        // 1. Находим или создаем пользователя в БД
        Subscriber subscriber = subscriberService.findOrCreateSubscriber(chatId, telegramUser);
        // 2. Получаем текущий статус участия
        boolean isActive = subscriber.isActive();
        // 3. Возвращаем приветственное сообщение с правильным статусом
        return messageService.createWelcomeMessage(chatId, isActive);
    }

    private SendMessage handleStatusCommand(Long chatId) {
        // 1. Получаем текущий статус участия
        boolean isActive = subscriberService.isUserActive(chatId);
        // 2. Меняем на противоположный
        subscriberService.activateUserParticipation(chatId);
        // 3. Возвращаем статусное сообщение с правильным статусом
        return messageService.createConfirmationMessage(chatId, isActive);
    }
}
