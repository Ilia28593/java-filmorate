package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_NAME_SIZE = 200;
    private int id;
    private final HashSet<Film> filmsSet = new HashSet<>();

    /**
     * Контроллер GET, отвечающий за запись в бд сущность Film.
     * @return возвращает код ответа с списком зарегестрированных Film.
     */
    @GetMapping
    public List<Film> allFilms() {
        log.info("Пользователей зарегастрировано: {}", filmsSet.size());
        return new ArrayList<>(filmsSet);
    }

    /**
     * Контроллер POST, отвечающий за запись в бд сущность Film.
     * @param film - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PostMapping
    public ResponseEntity<Film> postFilm(@RequestBody final Film film) throws ValidationException, ObjectAlreadyExistsException {
        if (!filmsSet.contains(film)) {
            log.info("Add request: {},{}", film, filmsSet.size());
            addFilm(checkConfigFilm(film));
            return ResponseEntity.status(HttpStatus.OK).body(film);
        } else {
            throw new ObjectAlreadyExistsException("User already exists");
        }
    }

    /**
     * Контроллер PUT, отвечающий за оббновление  в бд сущности Film.
     * @param film - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PutMapping
    public ResponseEntity<Film> putMet(@RequestBody final Film film) throws ValidationException, NotFoundException {
        if (checkContainFilms(film)) {
            log.info("Request for update: {}", film);
            updateFilm(checkConfigFilm(film));
            return ResponseEntity.status(HttpStatus.OK).body(film);
        } else {
            throw new NotFoundException("Film with this id was not found" + film.getId());
        }
    }

    private Film checkConfigFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name invalid");
        } else if (film.getDescription().length() > MAX_NAME_SIZE) {
            throw new ValidationException("Film description invalid");
        } else if (film.getReleaseDate().isBefore(FILM_BIRTHDAY)) {
            throw new ValidationException("Film release date invalid");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("Film duration invalid");
        } else {
            return film;
        }
    }

    @SuppressWarnings("checkstyle:WhitespaceAfter")
    private void updateFilm(Film film) {
        filmsSet.forEach(u -> {
            if (u.getId() == film.getId()) {
                u.setName(film.getName());
                u.setDescription(film.getDescription());
                u.setReleaseDate(film.getReleaseDate());
                u.setDuration(film.getDuration());
            }
            log.info("{}", film);
        });
    }

    private void addFilm(Film film) {
        film.setId(++id);
        filmsSet.add(film);
    }

    private boolean checkContainFilms(Film film) {
        return filmsSet.stream().anyMatch(u -> u.getId() == film.getId());
    }
}
