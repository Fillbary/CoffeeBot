package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Service.MessageService;
import com.example.CoffeeBot.Service.SubscriberService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class CallbackHandler {
    private final SubscriberService subscriberService;
    private final MessageService messageService;
    private final TelegramClient telegramClient;

    public CallbackHandler(SubscriberService subscriberService, MessageService messageService, TelegramClient telegramClientl) {
        this.subscriberService = subscriberService;
        this.messageService = messageService;
        this.telegramClient = telegramClientl;
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        // 1. Получаем текущий статус участия
        boolean isActive = subscriberService.isUserActive(callbackQuery.getMessage().getChatId());
        // 2. Получаем айди чата
        Long chatId = callbackQuery.getMessage().getChatId();

        // 3. Формируем ответ на нажатие кнопки в виде всплывающего окна
        AnswerCallbackQuery answer = messageService.createCallbackAnswer(callbackQuery.getId(), isActive);
        telegramClient.execute(answer);

        // 4. Отправляем сообщение подтверждение и меняем статус подписчика
        SendMessage updateMessage = messageService.createConfirmationMessage(chatId, isActive);
        subscriberService.activateUserParticipation(chatId);
        telegramClient.execute(updateMessage);
    }
}
