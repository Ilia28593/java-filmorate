package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserDbStorage userDbStorage;
    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("GET users");
        return userDbStorage.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        log.info("POST user");
        checkConfigUser(user);
        return userService.create(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT user");
        checkConfigUser(user);
        return userService.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends(@Valid @PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("PUT addFriends");
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriends(@Valid @PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("DELETE friend");
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@Valid @PathVariable Long id, @PathVariable Long otherId) {
        log.info("GET common friends");
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriendsById(@Valid @PathVariable("id") Long userId) {
        log.info("GET friend by id");
        return userService.getSetFriends(userId);
    }

    @GetMapping("/users/{id}")
    public User getUserById(@Valid @PathVariable("id") Long userId) {
        log.info("GET user by id");
        return userService.getUserById(userId);
    }

    private User checkConfigUser(User user) throws ValidationException {
        if (user.getLogin().isBlank()) {
            throw new ValidationException("User login is blank");
        } else if (user.getEmail().isBlank()) {
            throw new ValidationException("User email is blank");
        } else if (!EmailValidator.getInstance().isValid(user.getEmail())) {
            throw new ValidationException(String.format("User email invalid %s", user.getEmail()));
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(String.format("User birthday invalid %s", user.getBirthday()));
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            return user;
        } else {
            return user;
        }
    }
}
