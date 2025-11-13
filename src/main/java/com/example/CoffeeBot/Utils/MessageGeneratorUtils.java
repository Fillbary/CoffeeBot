package com.example.CoffeeBot.Utils;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class MessageGeneratorUtils {
    private static final String PAIR_MESSAGE_TEMPLATE = """
            ☕ <b>Кофе-митинг назначен!</b> ☕
                    
            Привет! На этой неделе твой партнер для кофе-митинга:
                    
            Никнейм: <b>%s</b>
            Имя: <b>%s</b>
            Фамилия: <b>%s</b>
                    
            Свяжись с партнером в удобное для вас время и назначьте встречу!
                    
            Хорошего общения! ☕✨
            """;
    private static final String TRIPLE_MESSAGE_TEMPLATE = """
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
    private static final String LONELY_MESSAGE_TEMPLATE = """
            🤷‍♂️ <b>На этой неделе пары нет</b> 🤷‍♀️
            <b>%s</b>
                    
            К сожалению, на этой неделе мы не смогли найти тебе пару для кофе-митинга.
                    
            Не расстраивайся! Попробуй:
            • Написать в общий чат и найти компанию самостоятельно
            • Присоединиться к другой паре
            • Перенести встречу на следующую неделю
                    
            Надеемся, в следующий раз повезет больше! ✨
            """;
    private static final String CANCELLATION_MESSAGE = """
                    🤷‍♂️ <b>Партнер отменил встречу</b>
                
            %s не сможет встретиться на этой неделе.
                
            Не беда! У вас будет новая возможность на следующей неделе.
                
            А пока можете:
            • Найти компанию в общем чате
            • Присоединиться к другой паре
            • Перенести встречу
                
            Удачи! ✨
            """;


    public static List<Subscriber> getMeetingSubscribers(CoffeeMeeting meeting) {
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

    public static String formatCancellationMessage(Subscriber cancelledBy) {
        return String.format(CANCELLATION_MESSAGE, cancelledBy.getUserName());
    }

    /**
     * Форматирует текст сообщения для парной встречи
     * Заполняет шаблон данными партнера
     *
     * @param partner партнер для встречи
     * @return отформатированный текст сообщения
     */
    public static String formatPairMessage(Subscriber partner) {
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
    public static String formatTripleMessage(Subscriber partner1, Subscriber partner2) {
        String[] partner1Data = formatSubscriberData(partner1);
        String[] partner2Data = formatSubscriberData(partner2);

        return String.format(TRIPLE_MESSAGE_TEMPLATE,
                partner1Data[0], partner1Data[1], partner1Data[2],
                partner2Data[0], partner2Data[1], partner2Data[2]);
    }

    public static String formatLonelyMessage(Subscriber lonelySubscriber) {

        return String.format(LONELY_MESSAGE_TEMPLATE, lonelySubscriber.getUserName());
    }

    private static String[] formatSubscriberData(Subscriber subscriber) {
        return new String[]{
                subscriber.getUserName() != null ? "@" + subscriber.getUserName() : "Не указан",
                subscriber.getFirstName() != null ? subscriber.getFirstName() : "Не указан",
                subscriber.getLastName() != null ? subscriber.getLastName() : "Не указан"
        };
    }
}
