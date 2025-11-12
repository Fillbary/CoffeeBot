package com.example.CoffeeBot.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import com.example.CoffeeBot.Utils.MessageKeyboardUtils;

@Service
@Slf4j
public class CreateMessageService {
    private static final String WELCOME_MESSAGE_TEMPLATE = """
            👋 <b>Добро пожаловать!</b>
                    
            Текущий статус участия: <b>%s</b>

            Для управления вашим участием используйте кнопку ниже:""";
    private static final String DEACTIVATE_MESSAGE_TEMPLATE = """
            ❌ <b>Участие деактивировано.</b>

            Вы больше не принимаете участие в активности.
            Статус: <b>%s</b>""";
    private static final String ACTIVATE_MESSAGE_TEMPLATE = """
            ✅ <b>Участие активировано!</b>

            Теперь вы принимаете участие в активности.
            Статус: <b>%s</b>""";

    /**
     * Создает приветственное сообщение для нового пользователя
     * Включает текущий статус участия и кнопку для управления
     *
     * @param chatId   идентификатор чата пользователя
     * @param isActive текущий статус активности пользователя
     * @return SendMessage с приветственным сообщением и клавиатурой
     */
    public SendMessage createWelcomeMessage(Long chatId, boolean isActive) {
        String statusText = isActive ? "активно" : "неактивно";
        String welcomeText = String.format(WELCOME_MESSAGE_TEMPLATE, statusText);
        String buttonText = isActive ? "❌ Отключить участие" : "✅ Принять участие";
        return MessageKeyboardUtils.createMessage(chatId, welcomeText, buttonText);
    }

    /**
     * Создает сообщение подтверждения изменения статуса участия
     * Используется после нажатия кнопки управления участием
     *
     * @param chatId   идентификатор чата пользователя
     * @param isActive предыдущий статус активности (до изменения)
     * @return SendMessage с подтверждением изменения статуса
     */
    public SendMessage createConfirmationMessage(Long chatId, boolean isActive) {
        String statusText = isActive ? "не активно" : "активно";
        String confirmText = isActive
                ? String.format(DEACTIVATE_MESSAGE_TEMPLATE, statusText)
                : String.format(ACTIVATE_MESSAGE_TEMPLATE, statusText);
        String buttonText = isActive ? "✅ Принять участие" : "❌ Отключить участие";

        return MessageKeyboardUtils.createMessage(chatId, confirmText, buttonText);
    }

    /**
     * Создает ответ на callback запрос от inline кнопки
     * Отображает всплывающее уведомление пользователю
     *
     * @param callbackId идентификатор callback запроса
     * @param isActive новый статус активности после изменения
     * @return AnswerCallbackQuery для отправки в Telegram
     */
    public AnswerCallbackQuery createCallbackAnswer(String callbackId, boolean isActive) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackId);
        String text = isActive ? "❌ Участие деактивировано!" : "✅ Участие активировано!";
        answer.setText(text);
        answer.setShowAlert(false);
        answer.setCacheTime(0);

        return answer;
    }
}