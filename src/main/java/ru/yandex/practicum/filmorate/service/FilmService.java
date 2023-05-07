package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService implements FilmStorage {

    private final FilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @Override
    public void addFilm(Film film) {
        if (!checkContainFilms(film)) {
            inMemoryFilmStorage.addFilm(film);
        } else {
            throw new ObjectAlreadyExistsException(String.format("This film %s is content in repository.", film.getName()));
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (checkContainFilms(film)) {
            inMemoryFilmStorage.updateFilm(film);
        } else {
            throw new NotFoundException(String.format("%s is not found from repository.", film.getName()));
        }
    }

    @Override
    public void removeFilm(Film film) throws NotFoundException {
        if (checkContainFilms(film)) {
            inMemoryFilmStorage.removeFilm(film);
        } else {
            throw new NotFoundException(String.format("%s is not found from repository", film.getName()));
        }
    }

    @Override
    public Set<Film> getSetFilm() {
        return inMemoryFilmStorage.getSetFilm();
    }

    public Film getFilm(long filmId) {
        return getSetFilm().stream().filter(f -> f.getId() == filmId).findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("%s is not found from repository", filmId)));
    }

    public boolean checkContainFilms(Film film) {
        return getSetFilm().stream().anyMatch(u -> u.getId() == film.getId());
    }

    public List<Film> getFavoriteFilms(int sizeList) {
        List<Film> df = getSetFilm().stream()
                .sorted(Comparator.comparingInt(o -> o.getLikeList().size()))
                .collect(Collectors.toList());
        Collections.reverse(df);
        log.info("getFavoriteFilms {}", df);
        return df.stream().limit(Math.min(getSetFilm().size(), sizeList)).collect(Collectors.toList());
    }

    public Film addLikesInFIlm(long idFilm, long userId) {
        getFilm(userId);
        getFilm(idFilm).getLikeList().add(userId);
        log.info("addLikesInFIlm {}", getFilm(idFilm));
        return getFilm(idFilm);
    }

    public Film removeLike(long idFilm, long idUser) {
        if (getFilm(idFilm).getLikeList().contains(idUser)) {
            getFilm(idFilm).getLikeList().remove(idUser);
            log.info("removeLike {}", getFilm(idFilm));
            return getFilm(idFilm);
        } else {
            throw new NotFoundException(String.format("%s is not found from repository", idUser));
        }
    }
}
