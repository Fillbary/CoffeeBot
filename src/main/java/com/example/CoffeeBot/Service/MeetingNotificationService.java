package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Utils.MessageGeneratorUtils;
import com.example.CoffeeBot.Utils.MessageKeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingNotificationService {
    private final PairMatchingService pairMatchingService;

    /**
     * Создает уведомления для всех пользователей о назначенных кофе-встречах
     * Генерирует пары на неделю и создает персонализированные сообщения для каждого участника
     *
     * @return список SendMessage с уведомлениями для отправки пользователям
     */
    public List<SendMessage> createNotificationsToUsers() {
        List<SendMessage> notificationsToUsers = new ArrayList<>();
        List<CoffeeMeeting> coffeeMeetings = pairMatchingService.generateWeeklyPairs();
        for (CoffeeMeeting coffeeMeeting : coffeeMeetings) {
            for (Subscriber subscriber : MessageGeneratorUtils.getMeetingSubscribers(coffeeMeeting)) {
                notificationsToUsers.add(createMeetingNotification(coffeeMeeting, subscriber));
            }
        }
        return notificationsToUsers;
    }

    public List<SendMessage> createCancellationNotificationsToUsers(CoffeeMeeting meeting, Subscriber cancelledBy) {
        List<SendMessage> notifications = new ArrayList<>();
        List<Subscriber> subscribers = MessageGeneratorUtils.getMeetingSubscribers(meeting);

        for (Subscriber subscriber : subscribers) {
            if (!subscriber.getId().equals(cancelledBy.getId())) {
                SendMessage message = createCancellationNotification(subscriber, cancelledBy);
                notifications.add(message);
            }
        }
        return notifications;
    }


    /**
     * Создает сообщение о назначенной встрече для конкретного подписчика
     * Определяет тип встречи (пара, тройка, одиночная) и генерирует соответствующий текст
     *
     * @param meeting    объект встречи CoffeeMeeting
     * @param subscriber подписчик, для которого создается сообщение
     * @return SendMessage с информацией о встрече
     */
    public SendMessage createMeetingNotification(CoffeeMeeting meeting, Subscriber subscriber) {
        String messageText = generateMessageText(meeting, subscriber);
        if(meeting.getSubscriber2() != null || meeting.getSubscriber3() != null) {
            return MessageKeyboardUtils.createMessageWithCancelButton(subscriber.getChatId(), messageText);
        } else {
            return MessageKeyboardUtils.createMessage(subscriber.getChatId(), messageText);
        }
    }


    public SendMessage createCancellationNotification(Subscriber subscriber, Subscriber cancelledBy) {
        String messageText = MessageGeneratorUtils.formatCancellationMessage(cancelledBy);
        return MessageKeyboardUtils.createMessage(subscriber.getChatId(), messageText);
    }

    /**
     * Генерирует текст сообщения в зависимости от типа встречи
     * Определяет является ли встреча парной, тройной или одиночной
     *
     * @param meeting    объект встречи CoffeeMeeting
     * @param subscriber подписчик, для которого генерируется сообщение
     * @return отформатированный текст сообщения
     */
    private String generateMessageText(CoffeeMeeting meeting, Subscriber subscriber) {
        List<Subscriber> partners = MessageGeneratorUtils.getMeetingSubscribers(meeting);
        if (meeting.getSubscriber3() != null) {
            return MessageGeneratorUtils.formatTripleMessage(partners.get(0), partners.get(1));
        } else if (meeting.getSubscriber2() != null) {
            return MessageGeneratorUtils.formatPairMessage(partners.get(0));
        } else {
            return MessageGeneratorUtils.formatLonelyMessage(subscriber);
        }

    }

}
