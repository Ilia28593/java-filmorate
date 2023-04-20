package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashSet<User> users = new HashSet<>();
    private final AtomicInteger id = new AtomicInteger();

    /**
     * Контроллер GET, отвечающий за запись в бд сущность User.
     * @return возвращает код ответа с списком зарегестрированных User.
     */
    @GetMapping
    public List<User> getUsers() {
        log.info("Number of users: {}", users.size());
        return new ArrayList<>(users);
    }

    /**
     * Контроллер POST, отвечающий за запись в бд сущность User.
     * @param user - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PostMapping
    ResponseEntity<User> createUser(@RequestBody final User user) throws ObjectAlreadyExistsException, ValidationException {
        if (!users.contains(user)) {
            log.info("Add request: {}", user);
            addFilm(checkConfigUser(user));
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            throw new ObjectAlreadyExistsException("User already exists");
        }
    }

    /**
     * Контроллер PUT, отвечающий за оббновление  в бд сущности User.
     * @param user - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PutMapping
    ResponseEntity<User> updateUser(@RequestBody User user) throws NotFoundException, ValidationException {
        if (checkContainUsers(user)) {
            log.info("Request for update: {}", user);
            updateInfoUser(checkConfigUser(user));
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            throw new NotFoundException("User with this id was not found: " + user.getId());
        }
    }

    private User checkConfigUser(User user) throws ValidationException {
        if (user.getLogin().isBlank()) {
            throw new ValidationException("User login invalid");
        } else if (user.getEmail().isBlank() || !EmailValidator.getInstance().isValid(user.getEmail())) {
            throw new ValidationException("User email invalid");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("User birthday invalid");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            return user;
        } else {
            return user;
        }
    }

    private void updateInfoUser(User user) {
        users.forEach(u -> {
            if (u.getId() == user.getId()) {
                u.setName(user.getName());
                u.setBirthday(user.getBirthday());
                u.setLogin(user.getLogin());
                u.setEmail(user.getEmail());
            }
            log.info("Update {} for bd: {}", user.getName(), user);
        });
    }

    private void addFilm(User user) {
        id.getAndIncrement();
        user.setId(id.get());
        log.info("Add for bd: {}", user);
        users.add(user);
    }

    private boolean checkContainUsers(User user) {
        return users.stream().anyMatch(u -> u.getId() == user.getId());
    }
}
