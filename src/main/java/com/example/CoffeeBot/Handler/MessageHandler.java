package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Service.MessageService;
import com.example.CoffeeBot.Service.SubscriberService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MessageHandler {
    private final SubscriberService subscriberService;
    private final MessageService messageService;
    private final TelegramClient telegramClient;

    public MessageHandler(SubscriberService subscriberService, MessageService messageService, TelegramClient telegramClient) {
        this.subscriberService = subscriberService;
        this.messageService = messageService;
        this.telegramClient = telegramClient;
    }

    public void handleMessage(Message message) throws TelegramApiException {
        String text = message.getText();
        Long chatId = message.getChatId();
        User telegramUser = message.getFrom();

        switch (text) {
            case "/start":
                handleStartCommand(chatId, telegramUser);
                break;
            case "/status":
                handleStatusCommand(chatId);
                break;
        }
    }

    private void handleStartCommand(Long chatId, User telegramUser) throws TelegramApiException {
        // 1. Находим или создаем пользователя в БД
        Subscriber subscriber = subscriberService.findOrCreateSubscriber(chatId, telegramUser);
        // 2. Получаем текущий статус участия
        boolean isActive = subscriber.isActive();
        // 3. Отправляем приветственное сообщение с правильным статусом
        SendMessage welcomeMessage = messageService.createWelcomeMessage(chatId, isActive);
        telegramClient.execute(welcomeMessage);
    }

    private void handleStatusCommand(Long chatId) throws TelegramApiException {
        // 1. Получаем текущий статус участия
        boolean isActive = subscriberService.isUserActive(chatId);
        // 2. Отправляем статусное сообщение с правильным статусом
        SendMessage statusMessage = messageService.createConfirmationMessage(chatId, isActive);
        // 3. И меняем на противоположный
        subscriberService.activateUserParticipation(chatId);
        telegramClient.execute(statusMessage);
    }
}
