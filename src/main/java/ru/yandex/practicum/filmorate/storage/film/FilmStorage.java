package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface FilmStorage {

    void addFilm(Film film);

    void updateFilm(Film film);

    void removeFilm(Film film);

    Set<Film> getSetFilm();
}
