package com.example.CoffeeBot.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class CoffeeMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime weekStartDate;

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
}
