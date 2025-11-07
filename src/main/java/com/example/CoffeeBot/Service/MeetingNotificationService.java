package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
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
    private final CreateMeetingMessageService createMeetingMessageService;
    private final PairMatchingService pairMatchingService;

    /**
     * Создает уведомления для всех пользователей о назначенных кофе-встречах
     * Генерирует пары на неделю и создает персонализированные сообщения для каждого участника
     *
     * @return список SendMessage с уведомлениями для отправки пользователям
     */
    public List<SendMessage> createNotificationsToUser() {
        List<SendMessage> notificationsToUsers = new ArrayList<>();
        List<CoffeeMeeting> coffeeMeetings = pairMatchingService.generateWeeklyPairs();
        for (CoffeeMeeting coffeeMeeting : coffeeMeetings) {
            for (Subscriber subscriber : getMeetingSubscribers(coffeeMeeting)) {
                notificationsToUsers.add(createNotificationToUser(coffeeMeeting, subscriber));
            }
        }
        return notificationsToUsers;
    }

    /**
     * Создает персонализированное уведомление для конкретного пользователя о его кофе-встрече
     * Сообщение содержит информацию о партнерах и детали встречи
     *
     * @param meeting встреча, для которой создается уведомление
     * @param subscriber подписчик, получающий уведомление
     * @return SendMessage с персонализированным уведомлением о встрече
     * @throws IllegalArgumentException если meeting или subscriber равны null
     */
    public SendMessage createNotificationToUser(CoffeeMeeting meeting, Subscriber subscriber) {
        if(meeting == null || subscriber == null) {
            throw new IllegalArgumentException("Missing a follower or meeting");
        }
        return createMeetingMessageService.createMeetingMessage(subscriber.getChatId(), meeting, subscriber);
    }

    /**
     * Извлекает всех участников кофе-встречи
     * Включает всех подписчиков встречи (от 1 до 3 участников)
     *
     * @param meeting встреча, из которой извлекаются участники
     * @return список всех участников встречи
     */
    private List<Subscriber> getMeetingSubscribers(CoffeeMeeting meeting) {
        List<Subscriber> meetingSubscribers = new ArrayList<>();

        if (meeting.getSubscriber1() != null) {
            meetingSubscribers.add(meeting.getSubscriber1());
        }
        if (meeting.getSubscriber2() != null) {
            meetingSubscribers.add(meeting.getSubscriber2());
        }
        if (meeting.getSubscriber3() != null) {
            meetingSubscribers.add(meeting.getSubscriber3());
        }

        return meetingSubscribers;
    }
}
