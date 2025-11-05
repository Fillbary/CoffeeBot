package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Service.MessageService;
import com.example.CoffeeBot.Service.SubscriberService;
import lombok.Getter;
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

    public CallbackDTO handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        // 1. Получаем текущий статус участия
        boolean isActive = subscriberService.isUserActive(callbackQuery.getMessage().getChatId());
        // 2. Получаем айди чата
        Long chatId = callbackQuery.getMessage().getChatId();
        // 3. Меняем статус подписчика после нажатия кнопки
        subscriberService.activateUserParticipation(chatId);

        // 4. Формируем ответ на нажатие кнопки в виде всплывающего окна
        AnswerCallbackQuery answer = messageService.createCallbackAnswer(callbackQuery.getId(), isActive);

        // 5. Формируем сообщение подтверждение
        SendMessage updateMessage = messageService.createConfirmationMessage(chatId, isActive);
        return new CallbackDTO(answer, updateMessage);
    }

    @Getter
    public record CallbackDTO(AnswerCallbackQuery answerCallbackQuery, SendMessage sendMessage) {
    }
}
