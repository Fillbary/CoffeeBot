package com.example.CoffeeBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoffeeBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeeBotApplication.class, args);
	}

}
