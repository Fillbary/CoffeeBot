package com.example.CoffeeBot.Config;

import com.example.CoffeeBot.CoffeeBot;
import com.example.CoffeeBot.Handler.ParticipationToggleHandler;
import com.example.CoffeeBot.Handler.MeetingCancellationHandler;
import com.example.CoffeeBot.Handler.MessageHandler;
import com.example.CoffeeBot.Repository.CoffeeMeetingRepository;
import com.example.CoffeeBot.Service.CreateMessageService;
import com.example.CoffeeBot.Service.MeetingNotificationService;
import com.example.CoffeeBot.Service.SubscriberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfig {

    @Value("${bot.token}")
    private String botToken;

    @Bean
    public TelegramBotsLongPollingApplication telegramBotApi() throws TelegramApiException {
        return new TelegramBotsLongPollingApplication();
    }

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botToken);
    }

    @Bean
    public MessageHandler messageHandler(SubscriberService subscriberService, CreateMessageService messageService) {
        return new MessageHandler(subscriberService, messageService);
    }

    @Bean
    public ParticipationToggleHandler callbackHandler(SubscriberService subscriberService, CreateMessageService messageService) {
        return new ParticipationToggleHandler(subscriberService, messageService);
    }

    @Bean
    public MeetingCancellationHandler MeetingCancellationHandler(
            SubscriberService subscriberService,
            CreateMessageService messageService,
            MeetingNotificationService meetingNotificationService,
            CoffeeMeetingRepository coffeeMeetingRepository) {
        return new MeetingCancellationHandler(subscriberService, messageService, meetingNotificationService, coffeeMeetingRepository);
    }

    @Bean
    public CoffeeBot coffeeBot(TelegramClient telegramClient, MessageHandler messageHandler, ParticipationToggleHandler callbackHandler) {
        return new CoffeeBot(telegramClient, messageHandler, callbackHandler);
    }

    @Bean
    public Boolean registerBot(TelegramBotsLongPollingApplication botsApplication,
                               CoffeeBot coffeeBot) throws TelegramApiException {
        botsApplication.registerBot(botToken, coffeeBot);
        return true;
    }
}
