package com.example.CoffeBot.Config;

import com.example.CoffeBot.CoffeeBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class BotConfig {

    private final String botUserName = "username";
    private final String botToken = "token";

    @Bean
    public TelegramBotsLongPollingApplication telegramBotApi() throws TelegramApiException {
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        botsApplication.registerBot(botToken, new CoffeeBot(botToken));
        return botsApplication;
    }

    @Bean
    public CoffeeBot coffeeBot() {
        CoffeeBot coffeeBot = new CoffeeBot(botToken);
        return coffeeBot;
    }
}
