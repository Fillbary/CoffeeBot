package com.example.CoffeeBot;

import com.example.CoffeeBot.Handler.MessageHandler;
import com.example.CoffeeBot.Handler.CallbackHandler;
import com.example.CoffeeBot.Handler.UpdateHandler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.util.List;

public class CoffeeBot implements LongPollingUpdateConsumer {
    private final UpdateHandler updateHandler;
    private final TelegramClient telegramClient;

    public String getBotUserName() {
        return "username";
    }

    public String getBotToken() {
        return "token";
    }

    public CoffeeBot(String botToken, UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            updateHandler.handleUpdate(update);
        }
    }
}
