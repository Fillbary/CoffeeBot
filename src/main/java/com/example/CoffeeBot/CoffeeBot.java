package com.example.CoffeeBot;

import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Service.MessageService;
import com.example.CoffeeBot.Service.SubscriberService;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public class CoffeeBot implements LongPollingUpdateConsumer {
    private final SubscriberService subscriberService;
    private final MessageService messageService;
    private final TelegramClient telegramClient;

    public String getBotUserName(){return "username";};

    public String getBotToken() {return "token";}

    public CoffeeBot(String botToken, SubscriberService subscriberService, MessageService messageService) {
        this.subscriberService = subscriberService;
        this.messageService = messageService;
        telegramClient = new OkHttpTelegramClient(botToken);}

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            handleUpdate(update);
        }
    }

    private void handleUpdate(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("toggle_participation")) {
                handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            // Логирование ошибок
            System.err.println("Error handling update: " + e.getMessage());
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
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

    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        boolean isActive = subscriberService.isUserActive(callbackQuery.getMessage().getChatId());
        Long chatId = callbackQuery.getMessage().getChatId();

        AnswerCallbackQuery answer = messageService.createCallbackAnswer(callbackQuery.getId(),isActive);
        telegramClient.execute(answer);

        SendMessage updateMessage = messageService.createConfirmationMessage(chatId, isActive);
        subscriberService.activateUserParticipation(chatId);
        telegramClient.execute(updateMessage);
    }
}
