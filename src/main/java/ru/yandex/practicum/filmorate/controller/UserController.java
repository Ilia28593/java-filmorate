package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashSet<User> users = new HashSet<>();
    private int id;

    @GetMapping
    public List<User> allUsers() {
        log.info("Пользователей зарегастрировано: {}", users.size());
        return new ArrayList<>(users);
    }

    @PostMapping
    ResponseEntity<User> postUser(@RequestBody final User user) throws ValidationException {
        if (!users.contains(user)) {
            log.info("{}", user);
            return checkUser(user);
        } else {
            throw new ValidationException("Пользователь с таким email или Login уже существует");
        }
    }

    @PutMapping
    ResponseEntity<User> putUser(@RequestBody User user) throws NotFoundException, ValidationException {
        if (checkContainUsers(user)) {
            log.info("{}", user);
            return checkUser(user);
        } else {
            throw new NotFoundException("Пользователь с таким id не найден" + user.getId());
        }
    }

    private ResponseEntity<User> checkUser(final User user) throws ValidationException {
        if (!user.getEmail().isBlank() && !user.getLogin().isBlank()
                && user.getBirthday().isBefore(LocalDate.now())
                && EmailValidator.getInstance().isValid(user.getEmail())) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                updateOrAddUser(user);
            } else {
                updateOrAddUser(user);
            }
            return ResponseEntity.status(200).body(user);
        } else {
            throw new ValidationException("Некоректно заполненые поля пользователя" + user.getName());
        }
    }

    public void updateOrAddUser(User user) {
        if (checkContainUsers(user)) {
            users.forEach(u -> {
                if (u.getId() == user.getId()) {
                    u.setName(user.getName());
                    u.setBirthday(user.getBirthday());
                    u.setLogin(user.getLogin());
                    u.setEmail(user.getEmail());
                }
            });
        } else {
            user.setId(++id);
            users.add(user);
        }
    }

    private boolean checkContainUsers(User user) {
        return users.stream().anyMatch(u -> u.getId() == user.getId());
    }
}
