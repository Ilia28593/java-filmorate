package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    public void addLike(long filmId, long userId) {
        filmDbStorage.filmById(filmId);
        String sqlQuery = "insert into film_likes (user_id, film_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    public void deleteLike(Long id, Long userId) {
        filmDbStorage.filmById(id);
        userDbStorage.userById(userId);
        String sqlQuery = "delete from film_likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    public List<Film> getPopular(Integer count) {
        List<Film> filmSet = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select films.ID, count(film_likes.USER_ID) as count_user  " +
                "from films left outer join film_likes on FILMS.ID = FILM_LIKES.FILM_ID group by FILMS.ID order by count_user desc limit ?", count);
        while (rs.next()) {
            filmSet.add(filmDbStorage.filmById((long) rs.getInt("id")));
        }
        return filmSet;
    }

    public Film getFilmById(Long id) {
        return filmDbStorage.filmById(id);
    }

    public Film createFilm(Film film) {
        return filmDbStorage.filmCreate(film);
    }

    public Film updateFilm(Film film) {
        return filmDbStorage.filmUpdate(film);
    }
}
