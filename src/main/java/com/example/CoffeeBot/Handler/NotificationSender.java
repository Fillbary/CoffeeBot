package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Service.MeetingNotificationService;
import com.example.CoffeeBot.Service.SubscriberService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class NotificationSender {
    private final SubscriberService subscriberService;
    private final MeetingNotificationService meetingNotificationService;
    private final TelegramClient telegramClient;

    public NotificationSender(SubscriberService subscriberService, MeetingNotificationService meetingNotificationService, TelegramClient telegramClient) {
        this.subscriberService = subscriberService;
        this.meetingNotificationService = meetingNotificationService;
        this.telegramClient = telegramClient;
    }

    public void sendNotification(CoffeeMeeting meeting, Subscriber subscriber) throws TelegramApiException {
        SendMessage notification = meetingNotificationService.createNotificationToUser(meeting, subscriber);
        telegramClient.execute(notification);
    }
}
