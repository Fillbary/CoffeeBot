package com.example.CoffeBot.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    private String firstName;

    private String lastName;

    @NotNull
    private String userName;

    private boolean isActive;

    private LocalDateTime registeredAt;
}
