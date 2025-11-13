package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Repository.CoffeeMeetingRepository;
import com.example.CoffeeBot.Service.CreateMessageService;
import com.example.CoffeeBot.Service.MeetingNotificationService;
import com.example.CoffeeBot.Service.SubscriberService;
import com.example.CoffeeBot.Utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Slf4j
@Component
public class MeetingCancellationHandler {
    private final SubscriberService subscriberService;
    private final CreateMessageService messageService;
    private final MeetingNotificationService meetingNotificationService;
    private final CoffeeMeetingRepository coffeeMeetingRepository;

    public MeetingCancellationHandler(
            SubscriberService subscriberService,
            CreateMessageService messageService,
            MeetingNotificationService meetingNotificationService,
            CoffeeMeetingRepository coffeeMeetingRepository) {
        this.subscriberService = subscriberService;
        this.messageService = messageService;
        this.meetingNotificationService = meetingNotificationService;
        this.coffeeMeetingRepository = coffeeMeetingRepository;
    }

    public CancelCallbackDTO handleCancelMeeting(CallbackQuery callbackQuery) {
        // 1. Получаем текущий статус участия
        boolean isActive = subscriberService.isUserActive(callbackQuery.getMessage().getChatId());
        // 2. Получаем айди чата
        Long chatId = callbackQuery.getMessage().getChatId();
        // 3. Находим пользователя, который отменил встречу
        Subscriber cancelledByUser = subscriberService.findByChatId(chatId);
        // 4. Находим встречу пользователя на текущей неделе
        CoffeeMeeting currentMeeting = coffeeMeetingRepository.findByWeekStartDate(DateUtils.getCurrentWeekMonday())
                .stream()
                .filter(meeting -> meeting.containsUser(cancelledByUser)) // используем метод из сущности
                .findFirst()
                .orElse(null);
        // 5. Деактивируем пользователя
        subscriberService.toggleUserParticipation(chatId);
        // 6. Создаем уведомления для партнеров
        List<SendMessage> partnerNotifications = meetingNotificationService
                .createCancellationNotificationsToUsers(currentMeeting, cancelledByUser);
        // 7. Создаем ответ для пользователя
        AnswerCallbackQuery answer = messageService.createCallbackAnswer(callbackQuery.getId(), isActive);
        SendMessage cancelMessage = messageService.createConfirmationMessage(chatId, isActive);
        return new CancelCallbackDTO(answer, cancelMessage, partnerNotifications);
    }

    public record CancelCallbackDTO(AnswerCallbackQuery answerCallbackQuery, SendMessage sendMessage, List<SendMessage> partnersNotifications) {}
}
