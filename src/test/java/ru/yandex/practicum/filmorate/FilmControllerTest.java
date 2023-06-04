package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class FilmControllerTest {
    protected Film film = new Film("Пандорум", "В безднах космоса движется звездолет.",
            LocalDate.of(2009, 10, 19), 108);
    private final FilmDbStorage filmDbStorage;
    private Validator validator;


    @BeforeEach
    void init() {
        film = new Film("Пандорум", "В безднах космоса движется звездолет.",
                LocalDate.of(2010, 10, 19), 108);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    private List<String> getValidateErrorMsg(Film validFilm) {
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        return violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    @Test
    void test1_addNewFilm() {
        filmDbStorage.filmCreate(film);
        List<Film> listFilm = filmDbStorage.findAll();
        assertEquals(2, listFilm.size(), "Список Film не корректный");
        assertEquals(film.getName(), listFilm.get(0).getName(), "названия фильмов не совпадают");
        assertEquals(film.getDescription(), listFilm.get(0).getDescription(), "описания фильмов не совпадают");
        assertEquals(film.getReleaseDate(), listFilm.get(0).getReleaseDate(), "Даты релиза не совпадают");
        assertEquals(film.getDuration(), listFilm.get(0).getDuration(), "Длительность фильма не совпадают");
    }

    @Test
    void test1_updateAndGetFilmById() {
        Film newFilm = filmDbStorage.filmCreate(film);
        newFilm.setMpa(new Mpa(1L,"G"));
        List<Genre> genreList = new ArrayList<>();
        genreList.add(new Genre(1, "Комедия"));
        newFilm.setGenres(genreList);
        filmDbStorage.filmUpdate(newFilm);
        Film newFilm2 = filmDbStorage.filmById(newFilm.getId());
        assertEquals(newFilm.getName(), newFilm2.getName(), "названия фильмов не совпадают");
        assertEquals(newFilm.getDescription(), newFilm2.getDescription(), "описания фильмов не совпадают");
        assertEquals(newFilm.getReleaseDate(), newFilm2.getReleaseDate(), "Даты релиза не совпадают");
        assertEquals(newFilm.getDuration(), newFilm2.getDuration(), "Длительность фильма не совпадают");
        assertEquals(newFilm.getGenres(), newFilm2.getGenres(), "Не добавляется жанр");
        assertEquals(newFilm.getMpa(), newFilm2.getMpa(), "Не меняется МРА");
    }

    @Test
    void test2_addNewFilmWithFailName() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> massages = getValidateErrorMsg(film);
        assertTrue(!violations.isEmpty(), "ошибка валидации при проверке класса");
        assertEquals(2, massages.size(), "Проверка на пустое имя не проходит");
    }

    @Test
    void test3_addNewFilmWithFaiDescription() {
        film.setDescription("В безднах космоса движется звездолет. Два члена команды, пробудившиеся от гиперсна, " +
                "оказываются в сложной ситуации: оборудование не работает, вспомнить они ничего не могут." +
                " Какова была их миссия? Сколько времени прошло? Где они? Кто они? На все эти вопросы у них " +
                "нет ответов. К тому же, на корабле обнаруживаются чужие - злобные воины, которые крушат все на" +
                " своем пути.\n" +
                "\n" +
                "У космических путешественников очень мало времени. Теперь для них собственные жизни - " +
                "не самое главное, ибо только от них двоих, возможно, зависит спасение человечества. " +
                "Нужно любой ценой восстановить управление кораблем, пока загадочный Пандорум не взял власть" +
                " в свои руки.");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> massages = getValidateErrorMsg(film);
        assertEquals(0, massages.size(), "Проверка на длину Description не проходит");
    }

    @Test
    void test5_addNewFilmWithFailDuration() {

        film.setDuration(-120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> massages = getValidateErrorMsg(film);
        assertTrue(!violations.isEmpty(), "продолжительность фильма должна быть положительной");
        assertEquals(1, massages.size(), "Проверка на положительный Duration не проходит");
    }
}
