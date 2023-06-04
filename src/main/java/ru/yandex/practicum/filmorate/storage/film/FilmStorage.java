package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {
    List<Film> findAll();

    Film filmCreate(Film film);

    Film filmUpdate(Film film);

    Film filmById(Long id);
}
