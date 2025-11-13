package com.example.CoffeeBot.Utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MessageKeyboardUtils {
     /**
     * Вспомогательный метод для создания сообщения с клавиатурой
     *
     * @param chatId идентификатор чата получателя
     * @param messageText текст сообщения
     * @param buttonText текст для кнопки
     * @return SendMessage с настроенным текстом и клавиатурой
     */
    public static SendMessage createMessageWithButton(Long chatId, String messageText, String buttonText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        message.setReplyMarkup(makeKeyboard(buttonText));
        message.setParseMode("HTML");
        return message;
    }

    /**
     * Создает сообщение с кнопкой отмены встречи
     *
     * @param chatId идентификатор чата получателя
     * @param messageText текст сообщения
     * @return SendMessage с кнопкой отмены встречи
     */
    public static SendMessage createMessageWithCancelButton(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        message.setReplyMarkup(makeCancelKeyboard());
        message.setParseMode("HTML");
        return message;
    }

    /**
     * Вспомогательный метод для создания сообщения
     *
     * @param chatId идентификатор чата получателя
     * @param messageText текст сообщения
     * @return SendMessage с настроенным текстом и клавиатурой
     */
    public static SendMessage createMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        message.setParseMode("HTML");
        return message;
    }

    /**
     * Создает inline клавиатуру с одной кнопкой для управления участием
     *
     * @param buttonText текст для отображения на кнопке
     * @return InlineKeyboardMarkup с настроенной кнопкой
     */
    public static InlineKeyboardMarkup makeKeyboard(String buttonText) {
        // Создаем кнопку
        InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
        button.setCallbackData("toggle_participation");
        // Создаем ряд кнопок используя InlineKeyboardRow
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        // Создаем список рядов
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        // Создаем InlineKeyboardMarkup с клавиатурой в конструкторе
        return new InlineKeyboardMarkup(keyboard);
    }

    public static InlineKeyboardMarkup makeCancelKeyboard() {
        InlineKeyboardButton button = new InlineKeyboardButton("❌ Отменить встречу");
        button.setCallbackData("cancel_meeting");

        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);

        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        return new InlineKeyboardMarkup(keyboard);
    }
}
