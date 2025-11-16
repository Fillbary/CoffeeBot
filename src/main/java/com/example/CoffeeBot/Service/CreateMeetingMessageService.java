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
// неиспользуемый класс
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
        return createSendMessage(chatId, messageText);
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
        List<Subscriber> partners = getPartners(meeting, subscriber);
        if (meeting.getSubscriber3() != null) {
            return formatTripleMessage(partners.get(0), partners.get(1));
        } else if (meeting.getSubscriber2() != null) {
            return formatPairMessage(partners.get(0));
        } else {
            return LONELY_MESSAGE_TEMPLATE;
        }

    }

    /**
     * Получает партнеров для текущего подписчика из встречи
     * Для парных встреч возвращает одного партнера, для тройных - двух партнеров
     *
     * @param meeting    объект встречи
     * @param subscriber текущий подписчик
     * @return список партнеров (1 для парной встречи, 2 для тройной встречи)
     */
    private List<Subscriber> getPartners(CoffeeMeeting meeting, Subscriber subscriber) {
        List<Subscriber> partners = new ArrayList<>();

        // Добавляем всех участников кроме текущего подписчика
        if (!meeting.getSubscriber1().equals(subscriber)) {
            partners.add(meeting.getSubscriber1());
        }
        if (!meeting.getSubscriber2().equals(subscriber)) {
            partners.add(meeting.getSubscriber2());
        }
        if (meeting.getSubscriber3() != null && !meeting.getSubscriber3().equals(subscriber)) {
            partners.add(meeting.getSubscriber3());
        }

        return partners;
    }

    /**
     * Создает объект SendMessage с HTML разметкой
     *
     * @param chatId      идентификатор чата
     * @param messageText текст сообщения
     * @return настроенный объект SendMessage
     */
    private SendMessage createSendMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        message.setParseMode("HTML");
        return message;
    }

    /**
     * Форматирует текст сообщения для парной встречи
     * Заполняет шаблон данными партнера
     *
     * @param partner партнер для встречи
     * @return отформатированный текст сообщения
     */
    private String formatPairMessage(Subscriber partner) {
        String[] partnerData = formatSubscriberData(partner);
        return String.format(PAIR_MESSAGE_TEMPLATE, partnerData[0], partnerData[1], partnerData[2]);
    }

    /**
     * Форматирует текст сообщения для тройной встречи
     * Заполняет шаблон данными двух партнеров
     *
     * @param partner1 первый партнер
     * @param partner2 второй партнер
     * @return отформатированный текст сообщения
     */
    private String formatTripleMessage(Subscriber partner1, Subscriber partner2) {
        String[] partner1Data = formatSubscriberData(partner1);
        String[] partner2Data = formatSubscriberData(partner2);

        return String.format(TRIPLE_MESSAGE_TEMPLATE,
                partner1Data[0], partner1Data[1], partner1Data[2],
                partner2Data[0], partner2Data[1], partner2Data[2]);
    }

    private String[] formatSubscriberData(Subscriber subscriber) {
        return new String[]{
                subscriber.getUserName() != null ? "@" + subscriber.getUserName() : "Не указан",
                subscriber.getFirstName() != null ? subscriber.getFirstName() : "Не указан",
                subscriber.getLastName() != null ? subscriber.getLastName() : "Не указан"
        };
    }
}
