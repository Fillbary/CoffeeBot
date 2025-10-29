package com.example.CoffeeBot.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    private Long chatId;

    private String firstName;

    private String lastName;

    @NotNull
    private String userName;

    private boolean isActive;

    private LocalDateTime registeredAt;
}
