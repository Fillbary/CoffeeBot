package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Service.MeetingNotificationService;
import com.example.CoffeeBot.Service.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {
    private final MeetingNotificationService meetingNotificationService;
    private final TelegramClient telegramClient;

    public void sendNotification() throws TelegramApiException {
        List<SendMessage> notifications = meetingNotificationService.createNotificationsToUser();
        for (SendMessage notification : notifications) {
            try {
                telegramClient.execute(notification);
                log.info("Notification sent to chat: {}", notification.getChatId());
                Thread.sleep(100);
            } catch (TelegramApiException e) {
                log.error("Failed to send notification to chat: {}", notification.getChatId(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Notification sending interrupted", e);
                break;
            }
        }
    }
}
