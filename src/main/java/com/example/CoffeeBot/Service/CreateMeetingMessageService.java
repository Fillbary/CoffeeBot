package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import com.example.CoffeeBot.Utils.MeetingMessageGenerator;

import java.util.List;

@Service
@Slf4j
public class CreateMeetingMessageService {


    /**
     * Создает сообщение о назначенной встрече для конкретного подписчика
     * Определяет тип встречи (пара, тройка, одиночная) и генерирует соответствующий текст
     *
     * @param chatId     идентификатор чата подписчика
     * @param meeting    объект встречи CoffeeMeeting
     * @param subscriber подписчик, для которого создается сообщение
     * @return SendMessage с информацией о встрече
     */
    public SendMessage createMeetingMessage(Long chatId, CoffeeMeeting meeting, Subscriber subscriber) {
        String messageText = generateMessageText(meeting, subscriber);
        String buttonText = subscriber.isActive() ? "❌ Отключить участие" : "✅ Принять участие";
        return MeetingMessageGenerator.createSendMessage(chatId, messageText, buttonText);
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
        List<Subscriber> partners = MeetingMessageGenerator.getPartners(meeting, subscriber);
        if (meeting.getSubscriber3() != null) {
            return MeetingMessageGenerator.formatTripleMessage(partners.get(0), partners.get(1));
        } else if (meeting.getSubscriber2() != null) {
            return MeetingMessageGenerator.formatPairMessage(partners.get(0));
        } else {
            return MeetingMessageGenerator.formatLonelyMessage(subscriber);
        }

    }
}
