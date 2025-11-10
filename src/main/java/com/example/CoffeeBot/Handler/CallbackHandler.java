package com.example.CoffeeBot.Handler;

import com.example.CoffeeBot.Service.CreateMessageService;
import com.example.CoffeeBot.Service.SubscriberService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Обработчик callback запросов от inline кнопок Telegram бота
 * Обрабатывает нажатия кнопок управления участием в кофе-митингах
 *
 * @see CreateMessageService
 * @see SubscriberService
 */
public class CallbackHandler {
    private final SubscriberService subscriberService;
    private final CreateMessageService createMessageService;

    public CallbackHandler(SubscriberService subscriberService, CreateMessageService createMessageService) {
        this.subscriberService = subscriberService;
        this.createMessageService = createMessageService;
    }

    /**
     * Обрабатывает входящий callback запрос от inline кнопки
     * Определяет тип callback данных и выполняет соответствующее действие
     *
     * @param callbackQuery объект callback запроса от Telegram
     * @return CallbackDTO содержащий ответ на callback и обновленное сообщение
     * @see CallbackDTO
     */
    public CallbackDTO handleCallbackQuery(CallbackQuery callbackQuery){
        // 1. Получаем текущий статус участия
        boolean isActive = subscriberService.isUserActive(callbackQuery.getMessage().getChatId());
        // 2. Получаем айди чата
        Long chatId = callbackQuery.getMessage().getChatId();
        // 3. Меняем статус подписчика после нажатия кнопки
        subscriberService.activateUserParticipation(chatId);

        // 4. Формируем ответ на нажатие кнопки в виде всплывающего окна
        AnswerCallbackQuery answer = createMessageService.createCallbackAnswer(callbackQuery.getId(), isActive);

        // 5. Формируем сообщение подтверждение
        SendMessage updateMessage = createMessageService.createConfirmationMessage(chatId, isActive);
        return new CallbackDTO(answer, updateMessage);
    }

    /**
     * Data Transfer Object для возврата результатов обработки callback запроса
     * Содержит ответ на callback запрос и обновленное сообщение для чата
     *
     * @param answerCallbackQuery ответ на callback запрос (всплывающее уведомление)
     * @param sendMessage обновленное сообщение для отправки в чат
     */
    public record CallbackDTO(AnswerCallbackQuery answerCallbackQuery, SendMessage sendMessage) {
    }
}
