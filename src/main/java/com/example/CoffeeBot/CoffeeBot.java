package com.example.CoffeeBot;

import com.example.CoffeeBot.Handler.MeetingCancellationHandler;
import com.example.CoffeeBot.Handler.ParticipationToggleHandler;
import com.example.CoffeeBot.Handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.util.List;

@Slf4j
public class CoffeeBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final MessageHandler messageHandler;
    private final MeetingCancellationHandler cancellationHandler;
    private final ParticipationToggleHandler participationHandler;

    public CoffeeBot(TelegramClient telegramClient,
                     MessageHandler messageHandler,
                     MeetingCancellationHandler cancellationHandler,
                     ParticipationToggleHandler participationHandler) {
        this.telegramClient = telegramClient;
        this.messageHandler = messageHandler;
        this.cancellationHandler = cancellationHandler;
        this.participationHandler = participationHandler;
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
        // Обработка текстовых сообщений
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }
        // Обработка callback-запросов (нажатия на кнопки)
        else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handleMessage(Update update) throws TelegramApiException {
        SendMessage response = messageHandler.handleMessage(update.getMessage());
        telegramClient.execute(response);
        log.info("Processed message from user: {}", update.getMessage().getFrom().getId());
    }

    private void handleCallbackQuery(Update update) throws TelegramApiException{
        String callBackData = update.getCallbackQuery().getData();

        if ("toggle_participation".equals(callBackData)) {
            handleToggleParticipation(update);
        } else if("cancel_meeting".equals(callBackData)) {
            handleCancelMeeting(update);
        }
    }

    private void handleToggleParticipation(Update update) throws TelegramApiException {
        ParticipationToggleHandler.CallbackDTO result =
                participationHandler.handleToggleParticipation(update.getCallbackQuery());

        telegramClient.execute(result.answerCallbackQuery());
        telegramClient.execute(result.sendMessage());
    }

    private void handleCancelMeeting(Update update) throws TelegramApiException {
        MeetingCancellationHandler.CancelCallbackDTO result =
                cancellationHandler.handleCancelMeeting(update.getCallbackQuery());

        telegramClient.execute(result.answerCallbackQuery());
        telegramClient.execute(result.sendMessage());

        for (SendMessage partnerNotification : result.partnersNotifications()) {
            telegramClient.execute(partnerNotification);
        }
    }
}
