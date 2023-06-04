package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    protected User user = new User("elfried@mqil.ru", "84489948", "Trail", LocalDate.of(1997, 10, 17));
    private final UserDbStorage userStorage;
    private Validator validator;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        user = new User("elfried@mqil.ru", "84489948", "Trail", LocalDate.of(1997, 10, 17));
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    private List<String> getValidateErrorMsg(User validUser) {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        return violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    @Test
    void test1_addNewUser() {
        userStorage.createUser(user);
        List<User> listUser = userStorage.findAll();

        assertEquals(1, listUser.size(), "Список User не корректный");
        assertEquals(user.getName(), listUser.get(0).getName(), "Имена не совпадают");
        assertEquals(user.getEmail(), listUser.get(0).getEmail(), "Email не совпадают");
        assertEquals(user.getBirthday(), listUser.get(0).getBirthday(), "Дата рождения не совпадают");
        assertEquals(user.getLogin(), listUser.get(0).getLogin(), "Логин не совпадают");
    }

    @Test
    void test2_updateNewAndGetUserById() {
        User newUser = userStorage.createUser(user);
        newUser.setName("New name");

        userStorage.updateUser(newUser);

        User newUser2 = userStorage.userById(newUser.getId());

        assertEquals(newUser.getEmail(), newUser2.getEmail(), "Email не совпадают");
        assertEquals(newUser.getBirthday(), newUser2.getBirthday(), "Дата рождения не совпадают");
        assertEquals(newUser.getLogin(), newUser2.getLogin(), "Логин не совпадают");
        assertEquals(newUser.getId(), newUser2.getId(), "Id не совпадают");
        assertEquals(newUser.getName(), newUser2.getName(), "Имена не совпадают");

    }

    @Test
    void test3_addNewUserWithFaiLogin() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> massages = getValidateErrorMsg(user);
        assertTrue(!violations.isEmpty(), "ошибка валидации при проверке класса");

        assertEquals(1, massages.size(), "Проверка не корректный логин не проходит");
    }

    @Test
    void test4_addNewUserWithFaiLogin() {
        user.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> massages = getValidateErrorMsg(user);
        assertTrue(!violations.isEmpty(), "ошибка валидации при проверке класса");

        assertEquals(1, massages.size(), "Проверка даты рождения не проходит");
    }

    @Test
    void test5_addNewUserWithFailEmail() {
        user.setEmail("o_kyzina.mail.ru@");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> massages = getValidateErrorMsg(user);
        assertTrue(!violations.isEmpty(), "ошибка валидации при проверке класса");

        assertEquals(1, massages.size(), "Проверка не корректный email не проходит");
    }
}

