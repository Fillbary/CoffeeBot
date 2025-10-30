package com.example.CoffeeBot.Handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateHandler {
    private MessageHandler messageHandler;
    private CallbackHandler callbackHandler;

    public UpdateHandler(MessageHandler messageHandler, CallbackHandler callbackHandler) {
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
    }

    public void handleUpdate(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                messageHandler.handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                callbackHandler.handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            System.err.println("Error handling update: " + e.getMessage());
        }
    }
}
