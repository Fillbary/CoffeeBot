package com.example.CoffeeBot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class CoffeeBotApplication implements CommandLineRunner {

    private final TelegramBotsLongPollingApplication botsApplication;
    private final CoffeeBot coffeeBot;
    private final String botToken;  // Получаем через конструктор

    public CoffeeBotApplication(TelegramBotsLongPollingApplication botsApplication,
                                CoffeeBot coffeeBot,
                                String botToken) {  // Spring инжектит бин токена
        this.botsApplication = botsApplication;
        this.coffeeBot = coffeeBot;
        this.botToken = botToken;
    }

    public static void main(String[] args) {
        SpringApplication.run(CoffeeBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            botsApplication.registerBot(botToken, coffeeBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("❌ Failed to register bot", e);
        }
    }
}