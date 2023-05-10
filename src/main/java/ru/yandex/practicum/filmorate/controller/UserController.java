package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Контроллер GET, отвечающий за запись в бд сущность User.
     *
     * @return возвращает код ответа с списком зарегестрированных User.
     */
    @GetMapping
    public List<User> getAllUsers() {
        log.info("Number of users: {}", userService.getUserList().size());
        return userService.getUserList();
    }

    /**
     * Контроллер POST, отвечающий за запись в бд сущность User.
     *
     * @param user - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody final User user) throws ObjectAlreadyExistsException, ValidationException {
        log.info("Add request: {}", user);
        userService.addUser(checkConfigUser(user));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * Контроллер PUT, отвечающий за оббновление  в бд сущности User.
     *
     * @param user - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PutMapping
    ResponseEntity<User> updateUser(@RequestBody User user) throws NotFoundException, ValidationException {
        log.info("Request for update: {}", user);
        userService.updateUser(checkConfigUser(user));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * Контроллер PUT, отвечающий за добавделение в друзья.
     *
     * @param id       - передается по http в заголовке запроса.
     * @param friendId - передается по http в заголовке запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PutMapping("/{id}/friends/{friendId}")
    ResponseEntity<User> addToFriends(@PathVariable long id, @PathVariable long friendId) {
        log.info("Request user for add to friend {}", friendId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.addForFriends(id, friendId));
    }

    /**
     * Контроллер DELETE, удаление из друзей.
     *
     * @param id       - передается по http в заголовке запроса.
     * @param friendId - передается по http в заголовке запроса.
     * @return возвращает код ответа с уже удаленным из друзей в бд сущностью.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    ResponseEntity<User> deleteToFriends(@PathVariable long id, @PathVariable long friendId) {
        log.info("Request user for delete to friend {}", friendId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.removeForFriends(id, friendId));
    }

    /**
     * Контроллер GET, отвечающий за поиск USER по id.
     *
     * @param id - передается по http в заголовке запроса.
     * @return возвращает друга.
     */
    @GetMapping("/{id}")
    ResponseEntity<User> getUser(@PathVariable long id) {
        log.info("Request user for getUser id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    /**
     * Контроллер GET, отвечающий за получения списка друзей.
     *
     * @param id - передается по http в заголовке запроса.
     * @return возвращает список дрйзей пользователя.
     */
    @GetMapping("/{id}/friends")
    ResponseEntity<List<User>> getListFriends(@PathVariable long id) {
        log.info("Request user for get to list friend {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.listFriends(id));
    }

    /**
     * Контроллер GET, отвечающий за получения списка  общих друзей.
     *
     * @param id      - передается по http в заголовке запроса.
     * @param otherId - передается по http в заголовке запроса.
     * @return возвращает список дрйзей общим с другим пользователем.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    ResponseEntity<List<User>> getListFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Request user for get to list friend {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.listGeneralFriends(id, otherId));
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
