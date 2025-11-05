package com.example.CoffeeBot;

import com.example.CoffeeBot.Handler.CallbackHandler;
import com.example.CoffeeBot.Handler.MessageHandler;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.util.List;

public class CoffeeBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;

    public CoffeeBot(TelegramClient telegramClient, MessageHandler messageHandler, CallbackHandler callbackHandler) {
        this.telegramClient = telegramClient;
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            try {
                handleUpdate(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleUpdate(Update update) throws TelegramApiException {
        if(update.hasMessage() && update.getMessage().hasText()) {
            SendMessage response = messageHandler.handleMessage(update.getMessage());
            telegramClient.execute(response);
        } else if (update.hasCallbackQuery()) {
            CallbackHandler.CallbackDTO CallbackDTO = callbackHandler.handleCallbackQuery(update.getCallbackQuery());
            telegramClient.execute(CallbackDTO.answerCallbackQuery());
            telegramClient.execute(CallbackDTO.sendMessage());
        }
    }
}
