package com.example.CoffeeBot.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;//

// @Data лучше не использовать для hibernate-сущностей
// https://thorben-janssen.com/lombok-hibernate-how-to-avoid-common-pitfalls/#avoid-data
@Data
@Entity
@NoArgsConstructor
public class CoffeeMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate weekStartDate;

    // модель данных конечно сойдет для простого проекта, но для более серьезного не очень
    // если надо будет добавить возможность делать встречи на 4-5-6 итд пользователей, придется каждый раз добавлять новые поля
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private Subscriber subscriber1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    private Subscriber subscriber2;

    @ManyToOne
    @JoinColumn(name = "user3_id")
    private Subscriber subscriber3;

    private LocalDateTime createdAt;

    public CoffeeMeeting(LocalDate weekStartDate, Subscriber subscriber1, Subscriber subscriber2) {
        this.weekStartDate = weekStartDate;
        this.subscriber1 = subscriber1;
        this.subscriber2 = subscriber2;
    }

    public CoffeeMeeting(LocalDate weekStartDate, Subscriber subscriber1, Subscriber subscriber2, Subscriber subscriber3) {
        this.weekStartDate = weekStartDate;
        this.subscriber1 = subscriber1;
        this.subscriber2 = subscriber2;
        this.subscriber3 = subscriber3;
    }

    // в этот метод достаточно передавать id, а не всего юзера. Вообще это хороший подход – передавать необходимый минимум данных
    public boolean containsUser(Subscriber user) {
        return (subscriber1 != null && subscriber1.getId().equals(user.getId())) ||
                (subscriber2 != null && subscriber2.getId().equals(user.getId())) ||
                (subscriber3 != null && subscriber3.getId().equals(user.getId()));
    }
}
