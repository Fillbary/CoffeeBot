package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CreateMeetingMessageService {
    private final String PAIR_MESSAGE_TEMPLATE = """
            ☕ <b>Кофе-митинг назначен!</b> ☕
                    
            Привет! На этой неделе твой партнер для кофе-митинга:
                    
            Никнейм: <b>%s</b>
            Имя: <b>%s</b>
            Фамилия: <b>%s</b>
                    
            Свяжись с партнером в удобное для вас время и назначьте встречу!
                    
            Хорошего общения! ☕✨
            """;
    private final String TRIPLE_MESSAGE_TEMPLATE = """
            👥 <b>Кофе-митинг втроем!</b> 👥
                    
            Привет! На этой неделе у тебя кофе-митинг с двумя коллегами:
                    
            Никнейм: <b>%s</b>
            Имя: <b>%s</b>
            Фамилия: <b>%s</b>
                    
            Никнейм: <b>%s</b>
            Имя: <b>%s</b>
            Фамилия: <b>%s</b>
                    
            Согласуйте время встречи и наслаждайтесь общением!
                    
            Интересной беседы! ☕✨
            """;
    private final String LONELY_MESSAGE_TEMPLATE = """
            🤷‍♂️ <b>На этой неделе пары нет</b> 🤷‍♀️
                    
            К сожалению, на этой неделе мы не смогли найти тебе пару для кофе-митинга.
                    
            Не расстраивайся! Попробуй:
            • Написать в общий чат и найти компанию самостоятельно
            • Присоединиться к другой паре
            • Перенести встречу на следующую неделю
                    
            Надеемся, в следующий раз повезет больше! ✨
            """;

    public SendMessage createMeetingMessage(Long chatId, CoffeeMeeting meeting, Subscriber subscriber) {
        String messageText = generateMessageText(meeting, subscriber);
        return createSendMessage(chatId, messageText);
    }

    private String generateMessageText(CoffeeMeeting meeting, Subscriber subscriber) {
        if (meeting.getSubscriber3() != null) {
            return generateTripleMessage(meeting, subscriber);
        } else if (meeting.getSubscriber2() != null) {
            return generatePairMessage(meeting, subscriber);
        } else {
            return LONELY_MESSAGE_TEMPLATE;
        }
    }

    private String generateTripleMessage(CoffeeMeeting meeting, Subscriber subscriber) {
        List<Subscriber> pairs = getPairs(meeting, subscriber);
        if (pairs.size() != 2) {
            throw new IllegalStateException("Triple meeting must have exactly 2 other participants");
        }
        return formatTripleMessage(pairs.get(0), pairs.get(1));
    }

    private String generatePairMessage(CoffeeMeeting meeting, Subscriber subscriber) {
        Subscriber pair = getPair(meeting, subscriber);
        return formatPairMessage(pair);
    }

    private List<Subscriber> getPairs(CoffeeMeeting meeting, Subscriber subscriber) {
        List<Subscriber> pairs = new ArrayList<>();

        if (!meeting.getSubscriber1().equals(subscriber)) {
            pairs.add(meeting.getSubscriber1());
        }
        if (!meeting.getSubscriber2().equals(subscriber)) {
            pairs.add(meeting.getSubscriber2());
        }
        if (meeting.getSubscriber3() != null && !meeting.getSubscriber3().equals(subscriber)) {
            pairs.add(meeting.getSubscriber3());
        }
        return pairs;
    }

    private Subscriber getPair(CoffeeMeeting meeting, Subscriber subscriber) {
        if (meeting.getSubscriber1().equals(subscriber)) {
            return meeting.getSubscriber2();
        } else {
            return meeting.getSubscriber1();
        }
    }

    private SendMessage createSendMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        message.setParseMode("HTML");
        return message;
    }

    private String formatPairMessage(Subscriber subscriber) {
        return String.format(PAIR_MESSAGE_TEMPLATE,
                subscriber.getUserName() != null ? "@" + subscriber.getUserName() : "Не указан",
                subscriber.getFirstName() != null ? subscriber.getFirstName() : "Не указан",
                subscriber.getLastName() != null ? subscriber.getLastName() : "Не указан");
    }

    private String formatTripleMessage(Subscriber subscriber1, Subscriber subscriber2) {
        return String.format(TRIPLE_MESSAGE_TEMPLATE,
                subscriber1.getUserName() != null ? "@" + subscriber1.getUserName() : "Не указан",
                subscriber1.getFirstName() != null ? subscriber1.getFirstName() : "Не указан",
                subscriber1.getLastName() != null ? subscriber1.getLastName() : "Не указан",
                subscriber2.getUserName() != null ? "@" + subscriber2.getUserName() : "Не указан",
                subscriber2.getFirstName() != null ? subscriber2.getFirstName() : "Не указан",
                subscriber2.getLastName() != null ? subscriber2.getLastName() : "Не указан");
    }
}
