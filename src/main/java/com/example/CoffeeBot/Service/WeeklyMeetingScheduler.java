package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Handler.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeeklyMeetingScheduler {
    private final NotificationSender notificationSender;

    /**
     * Планировщик для еженедельной генерации пар и отправки уведомлений
     * Выполняется каждый понедельник в 10:00 утра
     * Cron expression: "0 0 10 * * MON"
     * - 0 - секунды (0)
     * - 0 - минуты (0)
     * - 10 - часы (10:00)
     * - * - любой день месяца
     * - * - любой месяц
     * - MON - понедельник
     */
    @Scheduled(cron = "0 0 10 * * MON")
    public void scheduleWeeklyPairGeneration() {
        try {
            // Этот вызов автоматически:
            // 1. Сгенерирует пары через PairMatchingService
            // 2. Создаст уведомления через MeetingNotificationService
            // 3. Отправит сообщения через NotificationSender
            notificationSender.sendNotification();
            log.info("✅ Weekly coffee meeting generation completed successfully!");
        } catch (TelegramApiException e) {
            log.error("❌ Failed to generate weekly coffee meetings", e);
            // Здесь можно добавить отправку уведомления админу о проблеме
        }
    }

    /**
     * Дополнительный метод для тестирования планировщика
     * Можно запускать вручную или настроить для отладки
     */
    @Scheduled(cron = "0 0 12 * * FRI") // Каждую пятницу в 12:00 для теста
    public void testScheduler() {
        log.info("🧪 Test scheduler is working correctly");
    }
}
