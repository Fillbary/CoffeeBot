package com.example.CoffeeBot.Config;

import com.example.CoffeeBot.CoffeeBot;
import com.example.CoffeeBot.Service.MessageService;
import com.example.CoffeeBot.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class BotConfig {

    private final String botUserName = "BELZE_bot";
    private final String botToken = "5805073689:AAFgbNTbM1N09I7Ez7OvKhvCP3tvNcDHQQU";

    @Bean
    public TelegramBotsLongPollingApplication telegramBotApi() throws TelegramApiException {
        return new TelegramBotsLongPollingApplication();
    }

    @Bean
    public CoffeeBot coffeeBot(UserService userService, MessageService messageService) {
        return new CoffeeBot(botToken, userService, messageService);
    }

    @Bean
    public Boolean registerBot(TelegramBotsLongPollingApplication botsApplication,
                              CoffeeBot coffeeBot) throws TelegramApiException {
        botsApplication.registerBot(botToken, coffeeBot);
        return true;
    }
}
