package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.yandex.practicum.filmorate.Constance.FILM_BIRTHDAY_FIRST;
import static ru.yandex.practicum.filmorate.Constance.MAX_SIZE_DESCRIPTION;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    /**
     * Контроллер GET, отвечающий за запись в бд сущность Film.
     *
     * @return возвращает код ответа с списком зарегестрированных Film.
     */
    @GetMapping
    public List<Film> allFilms() {
        log.info("Пользователей зарегастрировано: {}", filmService.getSetFilm().size());
        return new ArrayList<>(filmService.getSetFilm());
    }

    /**
     * Контроллер POST, отвечающий за запись в бд сущность Film.
     *
     * @param film - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
  @PostMapping
    public ResponseEntity<Film> postFilm(@RequestBody final Film film) {
        log.info("Add request: {},{}", film, filmService.getSetFilm().size());
        filmService.addFilm(checkConfigFilm(film));
        log.info("Add request: {},{}", film, filmService.getSetFilm().size());
        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    /**
     * Контроллер PUT, отвечающий за оббновление  в бд сущности Film.
     *
     * @param film - передается по http в теле запроса.
     * @return возвращает код ответа с уже записанной в бд сущностью.
     */
    @PutMapping
public ResponseEntity<Film> putMet(@RequestBody final Film film) {
        log.info("Request for update: {}", film);
        filmService.updateFilm(checkConfigFilm(film));
        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    /**
     * Контроллер GET, отвечающий за поиск фильма.
     *
     * @param id - передается по http в заголовке  запроса.
     * @return возвращает фильм переданный в запросе.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Film> findFilm(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getFilm(id));
    }

    /**
     * Контроллер PUT, отвечающий за постановления лайка.
     *
     * @param id     - передается по http в заголовке запроса.
     * @param userId - передается по http в заголовке запроса.
     * @return возвращает фильм с поставленым лайком.
     */
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> likeInFilm(@PathVariable long id, @PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.addLikesInFIlm(id, userId));
    }

    /**
     * Контроллер DELETE, отвечающий за удаления лайка фильма.
     *
     * @param id     - передается по http в заголовке запроса.
     * @param userId - передается по http в заголовке запроса.
     * @return возвращает фильм с удаленным лайком.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLikeInFilm(@PathVariable long id, @PathVariable long userId) {
        log.info("Request for delete like filmId {}, from userId {}", id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(filmService.removeLike(id, userId));
    }

    /**
     * Контроллер GET, отвечающий за удаления лайка фильма.
     *
     * @param count - передается по http в заголовке запроса.
     * @return возвращает список фильмом с наибольшим количеством лайков.
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getFavoriteFilm(@RequestParam(defaultValue = "10", required = false) int count) {
        log.info("Request List friend size {}", count);
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getFavoriteFilms(count));
    }

    private Film checkConfigFilm(Film film) throws ValidationException {
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
