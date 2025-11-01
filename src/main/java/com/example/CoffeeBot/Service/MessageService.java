package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MessageService {
        private final String WELCOME_MESSAGE_TEMPLATE = """
            👋 <b>Добро пожаловать!</b>
        
            Текущий статус участия: <b>%s</b>

            Для управления вашим участием используйте кнопку ниже:""";
    private final String DEACTIVATE_MESSAGE_TEMPLATE = """
            ❌ <b>Участие деактивировано.</b>

            Вы больше не принимаете участие в активности.
            Статус: <b>%s</b>""";
    private final String ACTIVATE_MESSAGE_TEMPLATE = """
            ✅ <b>Участие активировано!</b>

            Теперь вы принимаете участие в активности.
            Статус: <b>%s</b>""";


    public SendMessage createWelcomeMessage(Long chatId, boolean isActive) {
        String statusText = isActive ? "активно" : "неактивно";
        String welcomeText = String.format(WELCOME_MESSAGE_TEMPLATE, statusText);
        String buttonText = isActive ? "❌ Отключить участие" : "✅ Принять участие";
        return createMessage(chatId, welcomeText, buttonText);
    }

    public SendMessage createConfirmationMessage(Long chatId, boolean isActive) {
        String statusText = isActive ? "не активно" : "активно";
        String confirmText = isActive
                ? String.format(DEACTIVATE_MESSAGE_TEMPLATE, statusText)
                : String.format(ACTIVATE_MESSAGE_TEMPLATE, statusText);
        String buttonText = isActive ? "✅ Принять участие" : "❌ Отключить участие";

        return createMessage(chatId, confirmText, buttonText);
    }

    public AnswerCallbackQuery createCallbackAnswer(String callbackId, boolean isActive) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackId);
        String text = isActive ? "❌ Участие деактивировано!" : "✅ Участие активировано!";
        answer.setText(text); // Текст уведомления
        answer.setShowAlert(false); // false - маленькое уведомление, true - alert окно
        answer.setCacheTime(0); // Время кэширования ответа

        return answer;
    }

    private InlineKeyboardMarkup makeKeyboard(String buttonText) {
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

    private SendMessage createMessage(Long chatId, String messageText, String buttonText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        message.setReplyMarkup(makeKeyboard(buttonText));
        message.setParseMode("HTML");
        return message;
    }


}