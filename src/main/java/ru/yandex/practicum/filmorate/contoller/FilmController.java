package ru.yandex.practicum.filmorate.contoller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashSet<Film> filmsSet = new HashSet<>();
    private int id;

    @GetMapping
    public List<Film> allFilms() {
        log.info("Пользователей зарегастрировано: {}", filmsSet.size());
        return new ArrayList<>(filmsSet);
    }

    @PostMapping
    ResponseEntity<Film> postFilm(@RequestBody final Film film) throws ValidationException {
        if (!filmsSet.contains(film)) {
            log.info("{},{}", film,filmsSet.size());
            return checkFilm(film);
        } else {
            throw new ValidationException("Данный фильм уже добавлен");
        }
    }

    @PutMapping
    Film putMet(@RequestBody final Film film) throws ValidationException, NotFoundException {
        if (checkContainFilms(film)) {
            log.info("{}", film);
            checkFilm(film);
            return film;
        } else {
            throw new NotFoundException("Пользователь с таким id не найден" + film.getId());
        }
    }

    private ResponseEntity<Film> checkFilm(Film film) throws ValidationException {
        if (!film.getName().isBlank() && film.getDescription().length() < 200
                && film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
                && film.getDuration() >= 0) {
            updateOrAddFilm(film);
            return ResponseEntity.status(200).body(film);
        } else {
            throw new ValidationException("Название не может быть пустым, максимальная длина описания —" +
                    "больше 200 символов, некоректная дата, продолжительность некоректная");
        }
    }

    private void updateOrAddFilm(Film film) {
        if (checkContainFilms(film)) {
            filmsSet.forEach(u -> {
                if (u.getId() == film.getId()) {
                    u.setName(film.getName());
                    u.setDescription(film.getDescription());
                    u.setReleaseDate(film.getReleaseDate());
                    u.setDuration(film.getDuration());
                }
            });
        } else {
            film.setId(++id);
            filmsSet.add(film);
        }
    }

    private boolean checkContainFilms(Film film) {
        return filmsSet.stream().anyMatch(u -> u.getId() == film.getId());
    }
}
