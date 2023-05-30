package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import javax.validation.Valid;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constance.FILM_BIRTHDAY_FIRST;
import static ru.yandex.practicum.filmorate.Constance.MAX_SIZE_DESCRIPTION;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;

    @GetMapping(value = "/films")
    public List<Film> findAll() {
        log.info("GET films");
        return filmDbStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("POST film");
        checkValidationFilm(film);
        return filmService.createFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT film");
        checkValidationFilm(film);
        return filmService.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@Valid @PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("PUT addLike");
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@Valid @PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("DELETE like");
        filmService.deleteLike(id, userId);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopular(@Valid @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        log.info("GET TOP10");
        return filmService.getPopular(count);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@Valid @PathVariable("id") Long id) {
        log.info(" GET film by id");
        return filmDbStorage.filmById(id);
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@Valid @PathVariable("id") Integer id) {
        log.info("GET film by id");
        return Mpa.forValues(id);
    }

    private Film checkValidationFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name invalid");
        } else if (film.getDescription().length() > MAX_SIZE_DESCRIPTION) {
            throw new ValidationException("Film description length invalid");
        } else if (film.getReleaseDate().isBefore(FILM_BIRTHDAY_FIRST)) {
            throw new ValidationException("Film release date invalid");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("Film duration invalid");
        } else {
            return film;
        }
    }
}
