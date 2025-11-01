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

    public SendMessage createNotificationToUser(CoffeeMeeting meeting, Subscriber subscriber) {
        if(meeting == null || subscriber == null) {
            throw new IllegalArgumentException("Missing a follower or meeting");
        }
        return createMeetingMessageService.createMeetingMessage(subscriber.getChatId(), meeting, subscriber);
    }

    public SendMessage sendMeetingNotifications() {
        // TODO
    }

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
