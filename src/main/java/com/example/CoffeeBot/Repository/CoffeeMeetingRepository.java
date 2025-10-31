package com.example.CoffeeBot.Repository;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CoffeeMeetingRepository extends JpaRepository<CoffeeMeeting, Long> {
    List<CoffeeMeeting> findByWeekStartDate(LocalDate startDate);
}
