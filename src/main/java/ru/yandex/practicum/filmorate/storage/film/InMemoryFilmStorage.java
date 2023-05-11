package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final AtomicInteger id = new AtomicInteger();

    private final HashSet<Film> filmsSet = new HashSet<>();

    @Override
    public void addFilm(Film film) {
        id.getAndIncrement();
        film.setId(id.get());
        filmsSet.add(film);
    }

    @Override
    public void updateFilm(Film film) {
        filmsSet.forEach(u -> {
            if (u.getId() == film.getId()) {
                u.setName(film.getName());
                u.setDescription(film.getDescription());
                u.setReleaseDate(film.getReleaseDate());
                u.setDuration(film.getDuration());
            }
        });
    }

    @Override
    public void removeFilm(Film film) {
        filmsSet.remove(film);
    }

    @Override
    public Set<Film> getSetFilm() {
        return filmsSet;
    }
}
