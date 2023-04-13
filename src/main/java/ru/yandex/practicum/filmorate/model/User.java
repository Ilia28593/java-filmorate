package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private String name = null;
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
