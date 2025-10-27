package com.example.CoffeeBot;

import com.example.CoffeeBot.Service.MessageService;
import com.example.CoffeeBot.Service.UserService;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public class CoffeeBot implements LongPollingUpdateConsumer {
    private final UserService userService;
    private final MessageService messageService;
    private final TelegramClient telegramClient;

    public String getBotUserName(){return "username";};

    public String getBotToken() {return "token";}


    public CoffeeBot(String botToken, UserService userService, MessageService messageService) {
        this.userService = userService;
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
            }
        } catch (Exception e) {
            // Логирование ошибок
            System.err.println("Error handling update: " + e.getMessage());
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
        String text = message.getText();
        Long chatId = message.getChatId();

        switch (text) {
            case "/start":
                SendMessage welcomeMessage = messageService.createWelcomeMessage(chatId);
                telegramClient.execute(welcomeMessage);
                break;
        }
    }

    private void handleStartCommand(Long chatId, User telegramUser) {

    }

    private void handleCommand(CallbackQuery callbackQuery) {

    }
}
