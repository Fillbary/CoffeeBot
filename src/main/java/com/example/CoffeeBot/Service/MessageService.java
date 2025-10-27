package com.example.CoffeeBot.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    public SendMessage createWelcomeMessage(Long chatId) {
        String welcomeText = """
                👋 <b>Добро пожаловать!</b>
                Для управления вашим участием используйте кнопку ниже:""";

        // Создаем кнопку
        InlineKeyboardButton button = new InlineKeyboardButton("🎮 Управление участием");
        button.setCallbackData("toggle_participation");

        // Создаем ряд кнопок используя InlineKeyboardRow
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);

        // Создаем список рядов
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        // Создаем InlineKeyboardMarkup с клавиатурой в конструкторе
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);

        // Создаем и возвращаем сообщение
        SendMessage message = new SendMessage(chatId.toString(), welcomeText);
        message.setReplyMarkup(markup);
        message.setParseMode("HTML");

        return message;
    }

    public SendMessage createConfirmationMessage(Long chatId, boolean isActive) {
        return null;
    }

    public AnswerCallbackQuery createCallbackAnswer(String callbackId, String text) {
        return null;
    }
}