package com.example.CoffeBot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public class CoffeeBot extends TelegramBotsLongPollingApplication implements LongPollingUpdateConsumer {

    public String getBotUserName(){return "username";};

    public String getBotToken() {return "token";}

    public void onUpdateReceived(Update update) {}

    private final TelegramClient telegramClient;

    public CoffeeBot(String botToken) {telegramClient = new OkHttpTelegramClient(botToken);}

    @Override
    public void consume(List<Update> list) {

    }
}
