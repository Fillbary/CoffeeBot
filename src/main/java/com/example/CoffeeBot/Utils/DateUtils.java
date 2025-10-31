package com.example.CoffeeBot.Utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtils {

     /**
     * Возвращает дату понедельника следующей недели
     * Используется для установки weekStartDate в CoffeeMeeting
     */
    public LocalDate getNextWeekMonday() {
        LocalDate today = LocalDate.now();
        return today.with(DayOfWeek.MONDAY).plusWeeks(1);
    }

    /**
     * Возвращает дату понедельника N недель назад от текущей даты
     * Используется для получения истории встреч за предыдущие недели
     */
    public LocalDate getPreviousWeek(int weeksBack) {
        if(weeksBack < 0) {
            throw new IllegalArgumentException("weeksBack cannot be negative: " + weeksBack);
        }

        LocalDate today = LocalDate.now();

        LocalDate currentWeekMonday = today.with(DayOfWeek.MONDAY);

        return currentWeekMonday.minusWeeks(weeksBack);
    }

    public LocalDate getCurrentWeekMonday() {
        return LocalDate.now().with(DayOfWeek.MONDAY);
    }

    public boolean isMonday(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.MONDAY;
    }
}
