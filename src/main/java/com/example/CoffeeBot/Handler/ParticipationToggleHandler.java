package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Service.CreateMessageService;
import com.example.CoffeeBot.Service.SubscriberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
public class ParticipationToggleHandler {
    private final SubscriberService subscriberService;
    private final CreateMessageService messageService;


    public ParticipationToggleHandler(SubscriberService subscriberService, CreateMessageService messageService) {
        this.subscriberService = subscriberService;
        this.messageService = messageService;
    }


    public CallbackDTO handleToggleParticipation(CallbackQuery callbackQuery) {
        boolean isActive = subscriberService.isUserActive(callbackQuery.getMessage().getChatId());
        Long chatId = callbackQuery.getMessage().getChatId();
        subscriberService.toggleUserParticipation(chatId);
        AnswerCallbackQuery answer = messageService.createCallbackAnswer(callbackQuery.getId(), isActive);
        SendMessage updateMessage = messageService.createConfirmationMessage(chatId, isActive);
        return new CallbackDTO(answer, updateMessage);
    }

    public record CallbackDTO(AnswerCallbackQuery answerCallbackQuery, SendMessage sendMessage) {}
}
